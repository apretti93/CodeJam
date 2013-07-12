package pkr;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import pkr.possTree.PossibilityNode;

import static pkr.possTree.PossibilityNode.TextureCategory;
import static pkr.possTree.PossibilityNode.WinningLosingCategory;
import static pkr.possTree.PossibilityNode.HandSubCategory;
import static pkr.possTree.PossibilityNode.HandCategory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class EvalHands {
    
    private static Logger log = LoggerFactory.getLogger(EvalHands.class);
    
    private static void checkUniqueness(List<Card> allCards)
    {
        boolean[] seenCard = new boolean[52];
        
        for (Card card : allCards) {
            
            
            if (seenCard[card.toInt()]) {
                log.warn("Cards {} ", allCards);
            }
            
            Preconditions.checkState(!seenCard[card.toInt()], card.toString());

            seenCard[card.toInt()] = true;
        }
    }
    
    private static List<Card> getAllCards(HoleCards[] cards, Flop flop, Card turn, Card river)
    {
        List<Card> allCards = Lists.newArrayList();

        if (cards!=null)
            for (HoleCards hCards : cards) {
                allCards.addAll(Arrays.asList(hCards.getCards()));            
            }
        
        allCards.addAll(Arrays.asList(flop.getCards()));
        
        if (turn != null)
            allCards.add(turn);
        
        if (river != null)
            allCards.add(river);
        
        return allCards;
    }
    
    public static CompleteEvaluation[] evaluate(boolean heroOnly, HoleCards[] cards, Flop flop, Card turn, Card river) {

        
        
        final int numPlayers = cards.length;
        
        //Add all cards to same list
        List<Card> allCards = getAllCards(cards, flop, turn, river);
                
        //Check uniqueness
        checkUniqueness(allCards);

        CompleteEvaluation[] evals = new CompleteEvaluation[cards.length];
        
        for(int i = 0; i < numPlayers; ++i) {
            evals[i] = evaluateSingleHand(heroOnly && i > 0, cards[i], flop, turn, river);
            
            //Evaluation flopEval = evaluateSingleHand(cards[i], flop, null, null);
            
           evals[i].setPosition(i);
        }
        
        populateFlopTexture(evals, 0, flop, null, null);
        populateFlopTexture(evals, 1, flop, turn, null);
        populateFlopTexture(evals, 2, flop, turn, river);
        
                    
        populateEvalutionNodeWithRelativeHandRankings(evals);
        
        
        
        return evals;
    }
    
    private static void populateFlopTexture(CompleteEvaluation[] evals, int round, Flop flop, Card turn, Card river) 
    {
        int[] freqCard = new int[NUM_RANKS];
        int[] freqSuit = new int[4];

        List<Card> allCards = getAllCards(null, flop, turn, river);
        Card[] allCardsArr = allCards.toArray(new Card[allCards.size()]);
        
        PossibilityNode flopTexture = new PossibilityNode( PossibilityNode.TextureCategory.values() );

        for (Card card : allCardsArr) {
           // suits |= 1 << card.getSuit().ordinal();            
            freqCard[card.getRank().getIndex()]++;
            freqSuit[card.getSuit().ordinal()]++;
        }
        
        
        
        boolean rainbow = true;
        
        for(int s = 0; s < 4; ++s)
        {
            if (freqSuit[s] == 2) {
                flopTexture.setFlag(PossibilityNode.TextureCategory.SAME_SUIT_2);
                rainbow = false;
            } else if (freqSuit[s] == 3) {
                flopTexture.setFlag(TextureCategory.SAME_SUIT_3);
                rainbow = false;
            }
        }
        
        if (rainbow) {
            flopTexture.setFlag(TextureCategory.UNSUITED);
        }
        
        for(CompleteEvaluation eval : evals) {
            eval.setPossibilityNode(round, 0, flopTexture);
        }
        
    }

    private static int[] straightBitMasks = {
            15 + (1 << 12), // a + 2 to 4
            31, 31 << 1, 31 << 2, 31 << 3, 31 << 4, 31 << 5, 31 << 6, 31 << 7,
            31 << 8 };
    
    private static final int NUM_RANKS = 13;

    /**
     * Calculates all non relative metrics for a single player given the flop/turn/river
     * for each round
     * 
     * @param scoreOnly
     * @param cards
     * @param flop
     * @param turn
     * @param river
     * @return
     */
    public static CompleteEvaluation evaluateSingleHand(boolean scoreOnly, HoleCards cards, Flop flop,  Card turn, Card river)
    {
        CompleteEvaluation eval = new CompleteEvaluation();

        eval.setHoleCards(cards);
        
        eval.setRoundScore(0, scoreSingleHand(cards, flop, null, null));
        eval.setRoundScore(1, scoreSingleHand(cards, flop, turn, null));
        eval.setRoundScore(2, scoreSingleHand(cards, flop, turn, river));
        
        if (!scoreOnly) {
         evaluateNodeSingleHand(
                eval.getPossibilityNode(0,1),eval.getPossibilityNode(0,2),eval.getPossibilityNode(0,3), 
                cards, eval.getRoundScore(0), flop, null, null );
         evaluateNodeSingleHand(
                eval.getPossibilityNode(1,1),eval.getPossibilityNode(1,2),eval.getPossibilityNode(0,3),
                cards, eval.getRoundScore(0), flop, turn, null );
        evaluateNodeSingleHand(
                eval.getPossibilityNode(2,1),eval.getPossibilityNode(2,2),eval.getPossibilityNode(0,3),
                cards, eval.getRoundScore(0), flop, turn, river );
        }
        
        return eval;
    }
    
    public static void evaluateNodeSingleHand(PossibilityNode winLose, 
            PossibilityNode handCat, PossibilityNode subHandCat, HoleCards cards, Score score, Flop flop,  Card turn, Card river)
    {
        
        
        //Top pair?
        if (score.handLevel == HandLevel.PAIR)
        {
            //exclude cases like 72  flop TT4
            
            if (!flop.isPaired && score.kickers[0] == flop.getSortedCards()[2].getRank()) 
            {
                handCat.setFlag(HandCategory.TOP_PAIR);
            }
        }
    }
    
    private static class CompareByRoundScore implements Comparator<CompleteEvaluation>
    {
        final int round;
        
        private CompareByRoundScore(int round) {
            super();
            this.round = round;
        }

        @Override
        public int compare(CompleteEvaluation o1, CompleteEvaluation o2)
        {
            return o1.getRoundScore(round).compareTo(o2.getRoundScore(round));
        }
    }
    
    public static void populateEvalutionNodeWithRelativeHandRankings( CompleteEvaluation[] eval ) {
     
        CompleteEvaluation[][] resultsSortedByRoundScore = new CompleteEvaluation[3][eval.length];
        
        for(int i = 0; i < eval.length; ++i) {
            resultsSortedByRoundScore[0][i] = eval[i];
            resultsSortedByRoundScore[1][i] = eval[i];
            resultsSortedByRoundScore[2][i] = eval[i];
        }
        
        for(int round = 0; round < 3; ++round) 
        {
            Arrays.sort(resultsSortedByRoundScore[round], new CompareByRoundScore(round));
            
            int sortedEvalIndex = eval.length - 1;
            final int bestHandIndex = eval.length - 1;
            
            final Score bestHandScore =  resultsSortedByRoundScore[round][bestHandIndex].getRoundScore(round);
            int numTiedForFirst = 0;
            
            for(; sortedEvalIndex >= 0; --sortedEvalIndex)
            {
                CompleteEvaluation curEval = resultsSortedByRoundScore[round][sortedEvalIndex]; 
                if (bestHandScore
                        .equals(curEval.getRoundScore(round)))
                {
                    curEval.setFlag(round, WinningLosingCategory.WINNING);
                    numTiedForFirst++;
                } else {
                    break;
                }
            }
            
            final int secondBestHandIndex = sortedEvalIndex;
            
            for(int winningEvalIndex = eval.length - 1; winningEvalIndex > secondBestHandIndex; --winningEvalIndex)
            {
                CompleteEvaluation winEval = resultsSortedByRoundScore[round][winningEvalIndex];            
                winEval.setRealEquity(1.0 / numTiedForFirst);
            }
            
            if (secondBestHandIndex >= 0) {
                //Second best hand exists
                final Score secondHandScore =  resultsSortedByRoundScore[round][secondBestHandIndex].getRoundScore(round);
                
                //either the 3rd best or -1 if no 3rd place
                int thirdBestHand = secondBestHandIndex - 1;
                
                while( thirdBestHand >= 0 && 
                        secondHandScore
                                .equals(resultsSortedByRoundScore[round][thirdBestHand].getRoundScore(round)))
                {
                    --thirdBestHand;
                }
                
                //Set flags for all losing hands
                for(sortedEvalIndex = secondBestHandIndex; sortedEvalIndex >= 0; --sortedEvalIndex) 
                {
                    CompleteEvaluation curEval = resultsSortedByRoundScore[round][sortedEvalIndex];
                    Score curScore = curEval.getRoundScore(round);
                
                    HandSubCategory cat = null;
                    if (bestHandScore.getHandLevel() != curScore.getHandLevel()) {
                        cat = HandSubCategory.BY_HAND_CATEGORY;
                    } else if (bestHandScore.getKickers()[0] != curScore.getKickers()[0]) {
                        cat = HandSubCategory.BY_KICKER_HAND;
                    } else if (bestHandScore.getKickers()[1] != curScore.getKickers()[1]) {
                        cat = HandSubCategory.BY_KICKER_1;
                    } else if (bestHandScore.getKickers()[2] != curScore.getKickers()[2]) {
                        cat = HandSubCategory.BY_KICKER_2_PLUS;
                    } else if (bestHandScore.getKickers()[3] != curScore.getKickers()[3]) {
                        cat = HandSubCategory.BY_KICKER_2_PLUS;
                    } else if (bestHandScore.getKickers()[4] != curScore.getKickers()[4]) {
                        cat = HandSubCategory.BY_KICKER_2_PLUS;
                    }
                    
                    Preconditions.checkNotNull(cat);
                    
                    curEval.setFlag(round, cat);
                    curEval.setRealEquity(0);
                    
                    if (sortedEvalIndex > thirdBestHand) {
                        curEval.setFlag(round, WinningLosingCategory.SECOND_BEST_HAND);
                    } else {
                        curEval.setFlag(round, WinningLosingCategory.LOSING);
                    }
                    
                    /*
                     * Set flags for best hand
                     */
                    if (sortedEvalIndex == secondBestHandIndex) {
                        //we can just used the flag for the 2nd best hand in the best hand as they won for the same reason the 2nd best hand lost
                        for(int winningEvalIndex = bestHandIndex; winningEvalIndex > secondBestHandIndex; --winningEvalIndex)
                        {
                            CompleteEvaluation winEval = resultsSortedByRoundScore[round][winningEvalIndex];
                        
                            winEval.setFlag(round, cat);
                        }
                    }
                }
                
                
                
            } else {
                //Second best hand does not exist; all way tie, just say that the all way tie won by a hand
                for(sortedEvalIndex = bestHandIndex; sortedEvalIndex >= 0; --sortedEvalIndex) {
                    CompleteEvaluation curEval = resultsSortedByRoundScore[round][sortedEvalIndex];
                    curEval.setFlag(round, HandSubCategory.BY_HAND_CATEGORY);
                }
            }
        }
        
    }
    
    
    
    public static Score scoreSingleHand(HoleCards cards, Flop flop,  Card turn, Card river) {
        
        Score score = new Score();
        
        List<Card> hand = Lists.newArrayList();

        hand.addAll(Arrays.asList(cards.getCards()));
        hand.addAll(Arrays.asList(flop.getCards()));
        if (turn != null)
            hand.add(turn);
        
        if (river != null)
            hand.add(river);
        Collections.sort(hand);

        
        int ranks = 0;

        int[] freqCard = new int[NUM_RANKS];
        int[] freqSuit = new int[4];

        boolean flush = false;
        Suit flushSuit = null;

        for (Card card : hand) {
           // suits |= 1 << card.getSuit().ordinal();
            ranks |= (1 << card.getRank().getIndex());
            freqCard[card.getRank().getIndex()]++;
            freqSuit[card.getSuit().ordinal()]++;

            if (freqSuit[card.getSuit().ordinal()] >= 5) {
                flush = true;
                flushSuit = card.getSuit();
            }
        }

        int straightRank = -1;

        for (int sbmIdx = straightBitMasks.length - 1; sbmIdx >= 0 ; --sbmIdx) {
            if ( (ranks & straightBitMasks[sbmIdx]) == straightBitMasks[sbmIdx] ) {
                straightRank = sbmIdx + 3;
                break;
            }
        }

        int fourKind = -1;
        int threeKind = -1;
        int firstPair = -1;
        int secondPair = -1;
        List<Integer> singleCard = Lists.newArrayList();

        for (int r = NUM_RANKS-1; r >= 0; --r) {
            if (freqCard[r] == 4) {
                fourKind = r;
            } else if (threeKind == -1 && freqCard[r] == 3) {
                threeKind = r;
                
            } else if ( (freqCard[r] == 2 || freqCard[r] == 3)  && firstPair == -1) {
                //first pair could be trips, ie 333222 really has a pair of 2's
                firstPair = r;
            } else if (freqCard[r] == 2 && secondPair == -1) {
                secondPair = r;
            } else if (freqCard[r] == 1) {
                singleCard.add(r);
            }
        }


        // straight flush
        if (straightRank >= 0 && flush) {
            score.setHandLevel(HandLevel.STRAIGHT_FLUSH);
            score.setKickers(
                    new CardRank[] { CardRank
                            .getFromZeroBasedValue(straightRank ) });
            return score;
        }

        // 4 kind
        if (fourKind >= 0) {
            score.setHandLevel(HandLevel.QUADS);

            // Find first non quad kicker
            for (int r = NUM_RANKS-1; r >= 0; --r) {
                if (freqCard[r] < 4 && freqCard[r] > 0) {
                    score
                            .setKickers(
                                    new CardRank[] { 
                                            CardRank.getFromZeroBasedValue(fourKind),
                                            CardRank
                                            .getFromZeroBasedValue(r) });
                    break;
                }
            }
            return score;
        }

        // full house
        if (threeKind >= 0 && firstPair >= 0) {

            score.setHandLevel(HandLevel.FULL_HOUSE);
            
            score
            .setKickers(
                    new CardRank[] { 
                            CardRank.getFromZeroBasedValue(threeKind)
                            ,CardRank.getFromZeroBasedValue(firstPair) });
            return score;
    
            
            
        }

        if (flush) {
           
            score.setHandLevel(HandLevel.FLUSH);

            score.setKickers(new CardRank[5]);

            // ace to two
            int kickerIndex = 0;
            for (int cardIdx = hand.size() - 1; cardIdx >= 0; --cardIdx) {
                Card card = hand.get(cardIdx);

                if (card.getSuit() != flushSuit)
                    continue;

                score.getKickers()[kickerIndex++] = card.getRank();

                if (kickerIndex == 5)
                    break;
            }
            return score;
        }

        // straight
        if (straightRank >= 0) {
            score.setHandLevel(HandLevel.STRAIGHT);
            score.setKickers(
                    new CardRank[] { CardRank
                            .getFromZeroBasedValue(straightRank ) });
            return score;
        }

        // 3 kind
        if (threeKind >= 0) {
            score.setHandLevel(HandLevel.TRIPS);

            int kickers = 1;

            score.setKickers(new CardRank[3]);

            score.getKickers()[0] = CardRank
                    .getFromZeroBasedValue(threeKind);

            for (int r = NUM_RANKS-1; r >= 0; --r) {
                if (freqCard[r] == 1) {
                    score.getKickers()[kickers++] = CardRank
                            .getFromZeroBasedValue(r);

                }

                if (kickers == 3)
                    break;
            }
            return score;
        }

        // 2 pair
        if (firstPair >= 0 && secondPair >= 0) {
            score.setHandLevel(HandLevel.TWO_PAIR);

            score.setKickers(new CardRank[3]);

            score.getKickers()[0] = CardRank
                    .getFromZeroBasedValue(firstPair);
            score.getKickers()[1] = CardRank
                    .getFromZeroBasedValue(secondPair);

            for (int r = NUM_RANKS-1; r >= 0; --r) {
                if (freqCard[r] >= 1 && r != firstPair && r != secondPair) {
                    score.getKickers()[2] = 
                            CardRank.getFromZeroBasedValue(r);
                    break;
                }

                
            }
            return score;

        }

        // Pair
        if (firstPair >= 0) {
            score.setHandLevel(HandLevel.PAIR);

            int kickers = 1;
            score.setKickers(new CardRank[4]);

            score.getKickers()[0] = CardRank
                    .getFromZeroBasedValue(firstPair);

            for (int r = NUM_RANKS-1; r >= 0; --r) {
                if (freqCard[r] == 1) {
                    score.getKickers()[kickers++] = CardRank
                            .getFromZeroBasedValue(r);

                }

                if (kickers == 4)
                    break;
            }
            return score;

        }

        // High card
        score.setHandLevel(HandLevel.HIGH_CARD);

        int kickers = 0;
        score.setKickers(new CardRank[5]);

        
        for (int r = NUM_RANKS-1; r >= 0; --r) {
            if (freqCard[r] == 1) {
                score.getKickers()[kickers++] = CardRank
                        .getFromZeroBasedValue(r);

            }

            if (kickers == 5)
                break;
        }
        return score;

    }
}
