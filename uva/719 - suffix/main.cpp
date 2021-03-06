//STARTCOMMON
#include <cmath>
#include <vector>
#include "stdio.h" 
#include <limits>
#include <cassert>
#include <string>
#include <iostream>

using namespace std;

#define mp make_pair 
#define pb push_back 
#define contains(c,x) ((c).find(x) != (c).end()) 
#define all(c) (c).begin(),(c).end() 
#define MIN(a,b) ((a)<(b)?(a):(b))
#define MAX(a,b) ((a)<(b)?(b):(a))

typedef unsigned int uint;
typedef long long ll;
typedef unsigned long long ull;

#define FORE(k,a,b) for(int k=(a); k <= (b); ++k)
#define FOR(k,a,b) for(int k=(a); k < (b); ++k)

typedef vector<int> vi; 
typedef vector<double> vd;
typedef vector<bool> vb;
typedef vector<vb> vvb;
typedef vector<vi> vvi;
typedef vector<uint> uvi; 
typedef vector<uvi> uvvi;
typedef vector<vd> vvd;
typedef pair<int,int> ii;
typedef pair<uint,uint> uu;
typedef vector<ii> vii;
typedef vector<vii> vvii;


const double tolerance = 0.000002;
template<typename T> 
int cmp(T a, T b, T epsilon = tolerance)
{
	T dif = a - b;
	if (abs(dif) <= epsilon)
	{
		return 0;
	}
	
	if (dif > 0)
	{
		return 1; //a > b
	}
	
	return -1;
}  


//STOPCOMMON

#include <cstring>
#include <string>

typedef pair<int, int> ii;

#define MAX_N 100010                              // second approach: O(n log n)

char T[MAX_N];                        // the input string, up to 100K characters
int n;                                             // the length of input string
int RA[MAX_N], tempRA[MAX_N];             // rank array and temporary rank array
int SA[MAX_N], tempSA[MAX_N];         // suffix array and temporary suffix array
int c[MAX_N];                                         // for counting/radix sort

char P[MAX_N];     // the pattern string (for string matching)
int m;             // the length of pattern string

int Phi[MAX_N];    // for computing longest common prefix
int PLCP[MAX_N];
int LCP[MAX_N];    // LCP[i] stores the LCP between previous suffix "T + SA[i-1]" and current suffix "T + SA[i]"


void countingSort(int k) {
  int i, sum, maxi = max(300, n);        // up to 255 ASCII chars or length of n
  memset(c, 0, sizeof c);                               // clear frequency table
  for (i = 0; i < n; i++)                    // count the frequency of each rank
    c[i + k < n ? RA[i + k] : 0]++;
  for (i = sum = 0; i < maxi; i++) {                     
    int t = c[i]; c[i] = sum; sum += t;
  }
  for (i = 0; i < n; i++)               // shuffle the suffix array if necessary
    tempSA[c[SA[i] + k < n ? RA[SA[i] + k] : 0]++] = SA[i];
  for (i = 0; i < n; i++)                          // update the suffix array SA
    SA[i] = tempSA[i];
}

void constructSA() {              // this version can go up to 100000 characters
  int i, k, r;
  for (i = 0; i < n; i++) RA[i] = T[i] - '.';                // initial rankings
  for (i = 0; i < n; i++) SA[i] = i;          // initial SA: {0, 1, 2, ..., n-1}
  for (k = 1; k < n; k <<= 1) {            // repeat sorting process log n times
    countingSort(k);       // actually radix sort: sort based on the second item
    countingSort(0);               // then (stable) sort based on the first item
    tempRA[SA[0]] = r = 0;                  // re-ranking; start from rank r = 0
    for (i = 1; i < n; i++)                         // compare adjacent suffices
      tempRA[SA[i]] =      // if same pair => same rank r; otherwise, increase r
        (RA[SA[i]] == RA[SA[i-1]] && RA[SA[i]+k] == RA[SA[i-1]+k]) ? r : ++r;
    for (i = 0; i < n; i++)                          // update the rank array RA
      RA[i] = tempRA[i];
} }

void computeLCP_slow() {
  LCP[0] = 0;                                                   // default value
  for (int i = 1; i < n; i++) {                     // compute LCP by definition
    int L = 0;                                            // always reset L to 0
    while (T[SA[i] + L] == T[SA[i-1] + L]) L++;   // L-th chars same, increase L
    LCP[i] = L;
} }

void computeLCP() {
  int i, L;
  Phi[SA[0]] = -1;                                              // default value
  for (i = 1; i < n; i++)                                 // compute Phi in O(n)
    Phi[SA[i]] = SA[i-1];         // remember which suffix is behind this suffix
  for (i = L = 0; i < n; i++) {                  // compute Permuted LCP in O(n)
    if (Phi[i] == -1) { PLCP[i] = 0; continue; }                 // special case
    while (T[i + L] == T[Phi[i] + L]) L++;    // L will be increased max n times
    PLCP[i] = L;
    L = max(L-1, 0);                          // L will be decreased max n times
  }
  for (i = 1; i < n; i++)                                 // compute LCP in O(n)
    LCP[i] = PLCP[SA[i]];   // put the permuted LCP back to the correct position
}

ii stringMatching() {                           // string matching in O(m log n)
  int lo = 0, hi = n-1, mid = lo;                 // valid matching = [0 .. n-1]
  while (lo < hi) {                                          // find lower bound
    mid = (lo + hi) / 2;                                   // this is round down
    int res = strncmp(T + SA[mid], P, m);       // try to find P in suffix 'mid'
    if (res >= 0) hi = mid;             // prune upper half (notice the >= sign)
    else          lo = mid + 1;                // prune lower half including mid
  }                                           // observe `=' in "res >= 0" above
  if (strncmp(T + SA[lo], P, m) != 0) return ii(-1, -1);         // if not found
  ii ans; ans.first = lo;

  lo = 0; hi = n - 1; mid = lo;
  while (lo < hi) {                 // if lower bound is found, find upper bound
    mid = (lo + hi) / 2;
    int res = strncmp(T + SA[mid], P, m);
    if (res > 0) hi = mid;                                   // prune upper half
    else         lo = mid + 1;                 // prune lower half including mid
  }                                // (notice the selected branch when res == 0)
  if (strncmp(T + SA[hi], P, m) != 0) hi--;                      // special case
  ans.second = hi;
  return ans;
} // return lower/upper bound as the first/second item of the pair, respectively

void LRS() {                          // print out the length and the actual LRS
  int i, maxLCP = 0;
  char ans[MAX_N];
  strcpy(ans, "");

  for (i = 1; i < n; i++)                                                // O(n)
    if (LCP[i] > maxLCP) {
      maxLCP = LCP[i];
      strncpy(ans, T + SA[i], maxLCP);             // copy 'len' characters only
      ans[maxLCP] = 0;                                    // null terminate this
    }

  printf("\nThe LRS is '%s' with length = %d\n", ans, maxLCP);
}

int owner(int idx) { return (idx < n-m-1) ? 1 : 2; }

void LCS() {                          // print out the length and the actual LCS
  int i, j, maxLCP = 0, idx = 0;
  char ans[MAX_N];
  strcpy(ans, "");

  printf("\nRemember, T = '%s'\nNow, enter another string P:\n", T);
  // T already has '.' at the back
  m = (int)strlen(gets(P));
  strcat(T, P);                                                      // append P
  strcat(T, "/");                                         // add '/' at the back
  n = (int)strlen(T);                                                // update n
  constructSA();                                                   // O(n log n)
  computeLCP();                                                          // O(n)
  printf("\nThe LCP information of 'T.P' = '%s':\n", T);
  printf("i\tSA[i]\tLCP[i]\tOwner\tSuffix\n");
  for (i = 0; i < n; i++)
    printf("%2d\t%2d\t%2d\t%2d\t%s\n", i, SA[i], LCP[i], owner(SA[i]), T + SA[i]);

  for (i = 1, maxLCP = -1; i < n; i++)
    if (LCP[i] > maxLCP && owner(SA[i]) != owner(SA[i-1])) {
      maxLCP = LCP[i];
      idx = i;
      //strncpy(ans, T + SA[i], maxLCP);
      //ans[maxLCP] = 0;
    }

  strncpy(ans, T + SA[idx], maxLCP);
  ans[maxLCP] = 0;
  printf("\nThe LCS is '%s' with length = %d\n", ans, maxLCP);
}

/*
int MinExp(const char *s, const int slen) {
	int i = 0, j = 1, k = 0, x, y, tmp;
	while(i < slen && j < slen && k < slen) {
		x = i + k;
		y = j + k;
		if(x >= slen)	x -= slen;
		if(y >= slen)	y -= slen;
		if(s[x] == s[y]) {
			k++;
		} else if(s[x] > s[y]) {
			i = j+1 > i+k+1 ? j+1 : i+k+1;
			k = 0;
			tmp = i, i = j, j = tmp;
		} else {
			j = i+1 > j+k+1 ? i+1 : j+k+1;
			k = 0;
		}
	}
	return i;
}
*/

/*
Finds alphabetically first representation of a cyclic string in O(length)
*/

/*
minimumExpression(s) {
   s = s + s;
   len = length(s), i = 0, j = 1, k = 0;
   while(i + k < len && j + k < len) {
      if(s[i+k] == s[j+k]) k++;
      else if(s[i+k] > s[j+k]) { i = i+k+1; if(i <= j) i = j+1; k = 0; }
      else if(s[i+k] < s[j+k]) { j = j+k+1; if(j <= i) j = i+1; k = 0; }
   }
   return min(i, j);
}*/

int main() 
{
	int nCases;
	scanf("%d", &nCases);
	gets(T);
	while(nCases--)
	{
		
		//int n;
	
		n = (int) strlen(gets(T));
		
		strncpy( T + n, T, n );
		
		n *= 2;
  
		T[n++] = '.';                                            // important bug fix!
		T[n] = '\0';
		//puts(T);
		constructSA();                                                   // O(n log n)
		
		int ans = 0;
		char* ansStr;
		
		//printf("\nThe Suffix Array of string T = '%s' is shown below (O(n log n) version):\n", T);
		//printf("i\tSA[i]\tSuffix\n");
		for (int i = 0; i < n; i++) 
		{
			if (!ans && SA[i] <= n / 2)
			{
				ans = SA[i] + 1;
				ansStr = T + SA[i];
			}
			//printf("%2d\t%2d\t%s\n", i, SA[i], T + SA[i]);
			
		}
		
		for(ans = 1; ans <= n / 2; ++ans)
		{
			if ( 0 == strncmp( T + ans - 1, ansStr, n / 2) )
			{
				printf("%d\n", ans);
				break;
			}
		}
		
		
		//cout << ansStr << endl;
	}
	
	return 0;
}




int maindemo() {
	int nCases;
	//scanf("%d", &nCases);
	nCases = 1;
	while(nCases--)
	{
		
		//int n;
	
  printf("Enter a string T below, we will compute its Suffix Array:\n");
  
  n = (int) strlen(gets(T));
  
  
  T[n++] = '.';                                            // important bug fix!
  constructSA();                                                   // O(n log n)
  printf("\nThe Suffix Array of string T = '%s' is shown below (O(n log n) version):\n", T);
  printf("i\tSA[i]\tSuffix\n");
  for (int i = 0; i < n; i++) printf("%2d\t%2d\t%s\n", i, SA[i], T + SA[i]);

  computeLCP();                                                          // O(n)
  LRS();        // find the longest repeated substring of the first input string

  printf("\nNow, enter a string P below, we will try to find P in T:\n");
  printf("Enter an empty string to stop this string search demo!\n");
  while (m = (int)strlen(gets(P)), m) {  // stop when an empty string is entered
    ii pos = stringMatching();
    if (pos.first != -1 && pos.second != -1) {
      printf("%s is found SA [%d .. %d] of %s\n", P, pos.first, pos.second, T);
      printf("They are:\n");
      for (int i = pos.first; i <= pos.second; i++)
        printf("  %s\n", T + SA[i]);
    } else printf("%s is not found in %s\n", P, T);
  }

  LCS();                    // find the longest common substring between T and P

  }
	return 0;
}
