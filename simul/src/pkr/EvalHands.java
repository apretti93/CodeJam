package pkr;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class EvalHands {
    
    private static Logger log = LoggerFactory.getLogger(EvalHands.class);
    
    public static Evaluation[] evaluate(HoleCards[] cards, Flop flop, Card turn, Card river) {

        boolean[] seenCard = new boolean[52];
        
        final int numPlayers = cards.length;
        
        List<Card> allCards = Lists.newArrayList();

        
        for (HoleCards hCards : cards) {
            allCards.addAll(Arrays.asList(hCards.getCards()));            
        }
        
        allCards.addAll(Arrays.asList(flop.getCards()));
        
        if (turn != null)
            allCards.add(turn);
        
        if (river != null)
            allCards.add(river);

                
        for (Card card : allCards) {
            
            
            if (seenCard[card.toInt()]) {
                log.warn("Cards {} flop {} turn {} river{}", cards, flop, turn, river);
            }
            
            Preconditions.checkState(!seenCard[card.toInt()], card.toString());

            seenCard[card.toInt()] = true;
        }

        Evaluation[] evals = new Evaluation[cards.length];
        
        for(int i = 0; i < numPlayers; ++i) {
            evals[i] = evaluateSingleHand(cards[i], flop, turn, river);
            evals[i].setPosition(i);
        }
        
        Evaluation[] resultsSortedByScore = new Evaluation[numPlayers];
        
        for(int i = 0; i < numPlayers; ++i) {
            resultsSortedByScore[i] = evals[i];
        }
        
        Arrays.sort(resultsSortedByScore);
        
        resultsSortedByScore[numPlayers - 1].setWon(true);
        resultsSortedByScore[numPlayers - 1].setRealEquity(1.0);
        
        if (resultsSortedByScore[numPlayers - 1].getScore().compareTo(
                resultsSortedByScore[numPlayers - 2].getScore())==0) {
            resultsSortedByScore[numPlayers - 1].setTied(true);
            int numTied = 1;
            
            int i = numPlayers - 2;
            
            for(; i >= 0; --i) {
                if (resultsSortedByScore[numPlayers - 1].getScore().compareTo(
                        resultsSortedByScore[i].getScore())==0) {
                    resultsSortedByScore[i].setTied(true);
                    ++numTied;
                } else {
                    break;
                }
            }
            
            for(int j = numPlayers - 1; j > i; --j) {
                resultsSortedByScore[j].setRealEquity(1.0 / numTied);
            }
            
        }
        
            
        
        
        return evals;
    }

    private static int[] straightBitMasks = {
            15 + (1 << 12), // a + 2 to 4
            31, 31 << 1, 31 << 2, 31 << 3, 31 << 4, 31 << 5, 31 << 6, 31 << 7,
            31 << 8 };
    
    private static final int NUM_RANKS = 13;

    @SuppressWarnings("unused")
    public static Evaluation evaluateSingleHand(HoleCards cards, Flop flop,  Card turn, Card river) {
        Evaluation eval = new Evaluation();

        eval.setHoleCards(cards);
        
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
            eval.getScore().setHandLevel(HandLevel.STRAIGHT_FLUSH);
            eval.getScore().setKickers(
                    new CardRank[] { CardRank
                            .getFromZeroBasedValue(straightRank ) });
            return eval;
        }

        // 4 kind
        if (fourKind >= 0) {
            eval.getScore().setHandLevel(HandLevel.QUADS);

            // Find first non quad kicker
            for (int r = NUM_RANKS-1; r >= 0; --r) {
                if (freqCard[r] < 4 && freqCard[r] > 0) {
                    eval.getScore()
                            .setKickers(
                                    new CardRank[] { 
                                            CardRank.getFromZeroBasedValue(fourKind),
                                            CardRank
                                            .getFromZeroBasedValue(r) });
                    break;
                }
            }
            return eval;
        }

        // full house
        if (threeKind >= 0 && firstPair >= 0) {

            eval.getScore().setHandLevel(HandLevel.FULL_HOUSE);
            
            eval.getScore()
            .setKickers(
                    new CardRank[] { 
                            CardRank.getFromZeroBasedValue(threeKind)
                            ,CardRank.getFromZeroBasedValue(firstPair) });
            return eval;
    
            
            
        }

        if (flush) {
           
            eval.getScore().setHandLevel(HandLevel.FLUSH);

            eval.getScore().setKickers(new CardRank[5]);

            // ace to two
            int kickerIndex = 0;
            for (int cardIdx = hand.size() - 1; cardIdx >= 0; --cardIdx) {
                Card card = hand.get(cardIdx);

                if (card.getSuit() != flushSuit)
                    continue;

                eval.getScore().getKickers()[kickerIndex++] = card.getRank();

                if (kickerIndex == 5)
                    break;
            }
            return eval;
        }

        // straight
        if (straightRank >= 0) {
            eval.getScore().setHandLevel(HandLevel.STRAIGHT);
            eval.getScore().setKickers(
                    new CardRank[] { CardRank
                            .getFromZeroBasedValue(straightRank ) });
            return eval;
        }

        // 3 kind
        if (threeKind >= 0) {
            eval.getScore().setHandLevel(HandLevel.TRIPS);

            int kickers = 1;

            eval.getScore().setKickers(new CardRank[3]);

            eval.getScore().getKickers()[0] = CardRank
                    .getFromZeroBasedValue(threeKind);

            for (int r = NUM_RANKS-1; r >= 0; --r) {
                if (freqCard[r] == 1) {
                    eval.getScore().getKickers()[kickers++] = CardRank
                            .getFromZeroBasedValue(r);

                }

                if (kickers == 3)
                    break;
            }
            return eval;
        }

        // 2 pair
        if (firstPair >= 0 && secondPair >= 0) {
            eval.getScore().setHandLevel(HandLevel.TWO_PAIR);

            eval.getScore().setKickers(new CardRank[3]);

            eval.getScore().getKickers()[0] = CardRank
                    .getFromZeroBasedValue(firstPair);
            eval.getScore().getKickers()[1] = CardRank
                    .getFromZeroBasedValue(secondPair);

            for (int r = NUM_RANKS-1; r >= 0; --r) {
                if (freqCard[r] >= 1 && r != firstPair && r != secondPair) {
                    eval.getScore().getKickers()[2] = 
                            CardRank.getFromZeroBasedValue(r);
                    break;
                }

                
            }
            return eval;

        }

        // Pair
        if (firstPair >= 0) {
            eval.getScore().setHandLevel(HandLevel.PAIR);

            int kickers = 1;
            eval.getScore().setKickers(new CardRank[4]);

            eval.getScore().getKickers()[0] = CardRank
                    .getFromZeroBasedValue(firstPair);

            for (int r = NUM_RANKS-1; r >= 0; --r) {
                if (freqCard[r] == 1) {
                    eval.getScore().getKickers()[kickers++] = CardRank
                            .getFromZeroBasedValue(r);

                }

                if (kickers == 4)
                    break;
            }
            return eval;

        }

        // High card
        eval.getScore().setHandLevel(HandLevel.HIGH_CARD);

        int kickers = 0;
        eval.getScore().setKickers(new CardRank[5]);

        
        for (int r = NUM_RANKS-1; r >= 0; --r) {
            if (freqCard[r] == 1) {
                eval.getScore().getKickers()[kickers++] = CardRank
                        .getFromZeroBasedValue(r);

            }

            if (kickers == 5)
                break;
        }
        return eval;

    }
}
