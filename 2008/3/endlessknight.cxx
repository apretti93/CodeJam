#include <fstream>
#include <iostream>
#include <vector>
#include <set>
#include <map>
#include <deque>
#include <queue>
#include <sstream>
#include <time.h>
#include <assert.h>
#include <boost/smart_ptr.hpp>
#define SHOW_TIME 0
#include "util.h" 
#include "bipartite.h"
#include <boost/math/common_factor.hpp>

#include <boost/shared_ptr.hpp>

using namespace std;


//#define LOG_OFF LOG_OFF

void do_test_case(int test_case, ifstream& input);

int main(int argc, char** args)
{
  if (argc < 2) {
    cerr << "Usage: <exe> input_file" << endl;
    return -1;
  }
  
  ifstream input;
  input.open(args[1]);
  
  int T;
  input >> T;

  SHOW_TIME_BEGIN(g) 
  	
  for (int test_case = 0; test_case < T; ++test_case) 
  {
    //try 
    {
      do_test_case(test_case, input);
    } 
    //catch(...) 
    {
      //error("Error exception caught\n"); 
    }
  }
  
  SHOW_TIME_END(g)
}

template<typename I> struct RowColGeneric
{
  I row;
  I col;
  
  RowColGeneric(I r, I c) : row(r), col(c) 
  {}
  
  int operator==(const RowColGeneric& rc) const
  {
    return row == rc.row && col == rc.col;
  }
  
  int operator!=(const RowColGeneric& rc) const
  {
    return !(*this == rc);
  }
};

  template<typename I> std::size_t hash_value(const RowColGeneric<I>& rc)
  {
      std::size_t seed = 0;
      boost::hash_combine(seed, rc.row);
      boost::hash_combine(seed, rc.col);
      return seed;
  }
  


template<typename I> ostream& operator<<(ostream& os, const RowColGeneric<I>& rc)
{
  os << rc.row+1 << ", " << rc.col+1;
  return os;
}

typedef RowColGeneric<unsigned int> RowCol;
typedef boost::unordered_set<RowCol> RockSet;
typedef set<pair<uint, uint> > RockLevelIndexSet;
typedef map<pair<uint, uint>, uint> RockMultipliers;
typedef unsigned int uint;

RowCol getRowCol(const unsigned int level, const unsigned int index)
{
  return RowCol(2 * level - index, level + index);
}

bool getLevelIndex(uint& level, uint& index, const RowCol& rc)
{
  level = (rc.row + rc.col) / 3;
  index = rc.col - level;
  
  if (getRowCol(level, index) != rc || index > level) {
    return false;
  } else {
    return true;
  }
}

struct Calc
{
  int count;
  typedef pair<uint, uint> PairUint;
  typedef boost::unordered_map<pair<uint, uint>, uint > Cache;
  Cache cache;
  
  Calc() : count(0) {}
  
  int calculate_unique_paths(const uint& level, const uint& index);
  int calculate_unique_paths2(const uint& level, const uint& index);
};

uint numHitsInRange(uint begin, uint end, uint num) {
  assert(end >= begin);
  return end / num - (begin >= 1 ? (begin-1) / num : 0);
  
}

int Calc::calculate_unique_paths(const uint& level, const uint& index)
{
  
  
  Cache::iterator c_it = cache.find(PairUint(level, index));
  
  if (c_it != cache.end()) {
     //return c_it->second;
  }
  
  uint denom_large = max(index, level-index);
  uint denom_small = min(index, level-index);
  
  LOG_ON();
  LOG(level);
  LOG(denom_large);
  LOG(denom_small);
  LOG_OFF();
  
  //level .. denom_large + 1
  //1 .. denom_small
  
  vector<uint> numerator;
  vector<uint> denom;
  
  if (level == index || index == 0) {
    return 1;
  }
  
  const uint num_start = denom_large + 1;
  const uint num_end = level;
  
  
  if (numHitsInRange(num_start, num_end, 10007) > 
    numHitsInRange(1, denom_small, 10007))
  {
    return 0;
  }
  
  if (index >= 10007 || level >= 10007) {
    uint index_1 = index / 10007;
    uint level_1 = level / 10007;
    uint index_0 = index % 10007;
    uint level_0 = level % 10007;
    LOG_ON(); 
    LOG(index_0);
    LOG(index_1);
    LOG(level_0);
    LOG(level_1);
    uint p1 = calculate_unique_paths(level_1, index_1);
    uint p0 = calculate_unique_paths(level_0, index_0);
    
    return p1 * p0 % 10007;
  }
  
  for(uint i = denom_large + 1; i <= level; ++i) {
    numerator.push_back(i); 
  }
  
  uint last_index_tried = level - (denom_large+1);
  
  for(uint den = denom_small; den > 1; --den) {
    bool trouve = false;
    uint den_remaining = den;
    
    const uint mult_begin = (num_start-1) / den + 1;
    const uint mult_end = num_end / den;
    
    for(uint mult = mult_begin; mult <= mult_end; ++mult) {
      LOG(mult);
      const uint num_to_try = mult * den;
      LOG(num_to_try);
      const uint index_to_try = num_to_try - num_start;
      LOG(numerator[index_to_try]);
    /*
    LOG(den);
    LOG(index_to_try);
    LOG(num_to_try);
    LOG(numerator[0]);
    LOG(numerator[numerator.size() - 1]);
    LOG(level);
    LOG(denom_large+1);
    */
      assert(index_to_try >= 0 && index_to_try < numerator.size());
    
      uint gcd = boost::math::gcd(numerator[index_to_try], den_remaining);
      assert(numerator[index_to_try] % gcd == 0);
      assert(den_remaining % gcd == 0);
      numerator[index_to_try] = numerator[index_to_try] / gcd;
      den_remaining = den_remaining / gcd;

      if (den_remaining == 1) {
        trouve = true;
        break;
      }
    }
    
    if(trouve) {
      continue;
    }
    //LOG_ON();
    LOG_STR("Doing " << den << " the hard way");
    LOG_OFF();
    for(vector<uint>::reverse_iterator it = numerator.rbegin(); it != numerator.rend(); ++it) {
      uint& num = *it;
      uint gcd = boost::math::gcd(num, den_remaining);
      //LOG(num);
      //LOG(gcd);
      assert(num % gcd == 0);
      assert(den % gcd == 0);
      num = num / gcd;
      den_remaining = den_remaining / gcd;
      if (den_remaining == 1) {
        trouve = true;
        break;
      }
    }
    
    if (!trouve) {
      LOG(den);
      throw den;
    }
      
  }

  uint r = 1;  
  for(vector<uint>::iterator it = numerator.begin(); it != numerator.end(); ++it) {
    uint& num = *it;
    r *= num;
    r %= 10007;
  }
  
  
  
  cache.insert(Cache::value_type(PairUint(level, index), r));
  return r;
}


int Calc::calculate_unique_paths2(const uint& level, const uint& index)
{
  Cache::iterator c_it = cache.find(PairUint(level, index));
  
  if (c_it != cache.end()) {
     return c_it->second;
  }
  
  uint r = 0;
  ++count;
  
  if (count % 1000 == 0) {
    //LOG_ON();
    LOG(count);
    LOG_OFF();
  }
  
  //LOG_OFF();
  //LOG_STR(level << " " << index);
  if (level == 0) {
    assert(index == 0);
    r = 1;
  } else { 
  
    assert(index >= 0 && index <= level);
    
    
    
    if (index > 0) {
      r += calculate_unique_paths(level - 1, index - 1);
    }
    
    if (index < level) {
      r += calculate_unique_paths(level - 1, index);
    }
  }
  
  r %= 10007;
  
  cache.insert(Cache::value_type(PairUint(level, index), r));
  return r;
}

void getRockLevelIndex(RockLevelIndexSet& rockLevelIndexSet, const RockSet& rockRowColSet)
{
  
  for(RockSet::const_iterator r_it = rockRowColSet.begin();
    r_it != rockRowColSet.end();
    ++r_it)
  {
    const RowCol& rockRC = *r_it;
    uint rockLevel, rockIndex;
    bool rockOnPath = getLevelIndex(rockLevel, rockIndex, rockRC);
    if (rockOnPath) {
      //LOG_ON();
      LOG(rockLevel);
      LOG(rockIndex);
      rockLevelIndexSet.insert(RockLevelIndexSet::value_type(rockLevel, rockIndex));
    }
  }
}

uint getUniquePaths(uint targetLevel, uint targetIndex, const RockMultipliers& rockMult);

//Multiplier is where the value of the rock
void getRockMultipliers(const RockLevelIndexSet& rockLevelIndexSet, RockMultipliers& rockMult)
{
  LOG_STR("getRockMultipliers");
  
  assert(rockMult.empty());
  
  for(RockLevelIndexSet::const_iterator r_it = rockLevelIndexSet.begin();
    r_it != rockLevelIndexSet.end();
    ++r_it)
  {
    uint rockLevel = r_it->first;
    uint rockIndex = r_it->second;
    
    LOG(rockLevel);
    LOG(rockIndex);
    
    uint reduceByMult = getUniquePaths(rockLevel, rockIndex, rockMult);
    LOG(reduceByMult);
    
    rockMult.insert(RockMultipliers::value_type(*r_it, reduceByMult));
  }
}


uint getUniquePaths(uint targetLevel, uint targetIndex, const RockMultipliers& rockMult)
{
  LOG_OFF();
  LOG_STR("getUniquePaths");
  RockSet empty;
  Calc c;
  //start with target
  int num_paths = c.calculate_unique_paths(targetLevel, targetIndex);
  LOG(num_paths);
  for(RockMultipliers::const_iterator r_it = rockMult.begin();
    r_it != rockMult.end();
        ++r_it)
  {
    uint rockLevel = r_it->first.first;
    uint rockIndex = r_it->first.second;
    
    LOG(rockLevel);
    LOG(rockIndex);
    
    if (rockIndex > targetIndex) {
      LOG_STR("Ignoring rock, index too Far");
      continue;
    }
    
    assert(rockLevel <= targetLevel);
    assert(rockIndex <= targetIndex);

      
    uint reduceByMult = r_it->second;
    LOG(reduceByMult);
      
    int adjLevel = targetLevel - rockLevel;
    int adjIndex = targetIndex - rockIndex;
    LOG(adjLevel);
    LOG(adjIndex);
      
    if (adjIndex > adjLevel) {
      LOG_STR("Ignoring rock, doesn't touch final square");
      continue;
    }
      
    int reduceBy = c.calculate_unique_paths(adjLevel, adjIndex);
      
    LOG(reduceBy);
    LOG(num_paths);
      //assert(reduceBy * reduceByMult <= num_paths);
      //num_paths += 10007;
    num_paths -= (reduceBy * reduceByMult);
    num_paths %= 10007;
    if (num_paths < 0) {
      LOG(num_paths);
      num_paths += 10007;
    }
    LOG(num_paths);
    LOG_OFF();
  }
  
  return num_paths;
}

void do_test_calc() {
    const uint level_to_test = 10000;
  for(uint index_to_test = 5000; index_to_test <= level_to_test; ++index_to_test) {
    Calc c;
    SHOW_TIME_BEGIN(g) 
    LOG(index_to_test);
    uint c1 = c.calculate_unique_paths(level_to_test, index_to_test);
    LOG_ON();
    LOG(c1);
    SHOW_TIME_END(g)
    }

}

void do_test_range() {
  assert(numHitsInRange(0, 2, 3) == 0);
  assert(numHitsInRange(0, 3, 3) == 1);
  assert(numHitsInRange(0, 4, 3) == 1);
  assert(numHitsInRange(3, 3, 3) == 1);
  assert(numHitsInRange(3, 7, 3) == 2);
  assert(numHitsInRange(2, 19, 3) == 6);
  assert(numHitsInRange(3, 19, 3) == 6);
  assert(numHitsInRange(4, 19, 3) == 5);
  assert(numHitsInRange(4, 18, 3) == 5);
  assert(numHitsInRange(4, 17, 3) == 4);
  //assert(false);
}

void do_comp_calc() {
  LOG_OFF();
  Calc c;
  const uint level_to_test = 371;
  for(uint index_to_test = 0; index_to_test <= level_to_test; ++index_to_test) {
  
  SHOW_TIME_BEGIN(g)
  
  LOG(index_to_test);
  uint c1 = c.calculate_unique_paths(level_to_test, index_to_test);
  //LOG_ON();
  LOG(c1);
    //SHOW_TIME_END(g)
  SHOW_TIME_BEGIN(g2)
  LOG_OFF();
  uint c2 = c.calculate_unique_paths2(level_to_test, index_to_test);
  //LOG_ON();
  LOG(c2);
  if (c1 != c2) 
  {
    throw '3';
  }
    //SHOW_TIME_END(g2)
  }

}

void do_test_case(int test_case, ifstream& input)
{
  do_test_range();
  
  //do_comp_calc();
  
  //do_test_calc();
  //return;
  LOG_OFF();
  unsigned int H, W, R;
  input >> H >> W >> R;
  
  RockSet rocks;
  
  for(int i = 0; i < R; ++i) {
    unsigned int r, c;
    input >> r >> c;
    RowCol rc(r -1, c - 1);
    rocks.insert(rc);
    LOG(rc);
  }
  
  RockSet testRocks;
  testRocks.insert(RowCol(3-1, 2-1));
  
  assert(isMember(testRocks, RowCol(2, 1)));
  //LOG_ON();
  LOG_OFF();
  for(int level = 0; level < 20; ++level)
  {
    LOG(level);
    for(int i = 0; i <= level; ++i)
    {
      LOG_STR(level << ", " << i << " = rc: " << getRowCol(level, i));
    }
  }
  //return;
  RowCol target(H-1, W-1);

  uint level, index;  
  bool possible = getLevelIndex(level, index, target);
  
  LOG_ON();
  LOG_STR("Possible " << possible);
  LOG_STR("Final Level: " << level);
  LOG_STR("Final index: " << index);
  LOG_STR("Final Target: " << target);
  LOG_OFF();
  
  uint num_paths = 0;
  
  if (possible) {
    RockLevelIndexSet rockLevelIndexSet;
    
    getRockLevelIndex(rockLevelIndexSet, rocks);
    
    RockMultipliers rockMultipliers;
    
    getRockMultipliers(rockLevelIndexSet, rockMultipliers);
    LOG_STR("Final get unique");
    num_paths = getUniquePaths(level, index, rockMultipliers);
  }
  
  printf("Case #%d: %d\n", test_case+1, num_paths);
   
  return;    
}
  
