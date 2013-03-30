// link: http://uva.onlinejudge.org/external/114/11420.html
// Runtime: 0.364s
// Tag: Recur, dp
 
 
//============================================================================
// Name        : Test.cpp
// Author      : Shahab
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================
 
// @BEGIN_OF_SOURCE_CODE
 
#include <iostream>
#include <cstdio>
#include <algorithm>
#include <cstring>
#include <string>
#include <cctype>
#include <stack>
#include <queue>
#include <list>
#include <vector>
#include <map>
#include <sstream>
#include <cmath>
#include <bitset>
#include <utility>
#include <set>
#include <numeric>
#include <ctime>
 
#define Inf 2147483647
#define Pi acos(-1.0)
#define N 50000
#define LL long long
 
#define F(i, a, b) for( int i = (a); i < (b); i++ )
#define Fs(i, sz) for( size_t i = 0; i < sz.size (); i++ )
#define Fe(it, x) for(typeof (x.begin()) it = x.begin(); it != x.end (); it++)
#define Pr(x) for(typeof (x.begin()) it = x.begin(); it != x.end (); it++) cout << *it << " "; cout << endl;
#define Set(a, s) memset(a, s, sizeof (a))
#define Rd(r) freopen(r, "r", stdin)
#define Wt(w) freopen(w, "w", stdout)
 
using namespace std;
 
enum State {UnLocked, Locked, SecureLocked};
 
LL dp [65 + 3] [3 + 2] [65 + 3];
int n, s;
 
LL recur (int at, State pre, int cntSecure)
{
    if ( at == n ) return cntSecure == s ? 1 : 0;
 
    if ( dp [at] [pre] [cntSecure] != -1 ) return dp [at] [pre] [cntSecure];
 
    LL c = 0;
 
    if ( pre == UnLocked ) {
        c += recur (at + 1, UnLocked, cntSecure);
        c += recur (at + 1, Locked, cntSecure);
    } else if ( pre == Locked ) {
        c += recur (at + 1, UnLocked, cntSecure);
        c += recur (at + 1, SecureLocked, cntSecure + 1);
    } else {
        c += recur (at + 1, UnLocked, cntSecure);
        c += recur (at + 1, SecureLocked, cntSecure + 1);
    }
 
    return dp [at] [pre] [cntSecure] = c;
}
 
int main ()
{
    while ( scanf ("%d %d", &n, &s) ) {
        if ( n < 0 && s < 0 ) break;
 
        Set (dp, -1);
 
        printf ("%lld\n", recur (0, Locked, 0));
    }
 
    return 0;
}
 
// @END_OF_SOURCE_CODE