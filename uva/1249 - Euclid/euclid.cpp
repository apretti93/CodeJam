#include <iostream>
#include <fstream>
#include <string>
#include <map>
#include <stack>
#include <set>
#include <list>
#include <vector>
#include <algorithm>
#include <cassert>
#include <iterator>
#include <sstream>
//#include <complex>
#include <bitset>
#include <iomanip>
#include <cctype>
#include <limits>
#include <numeric>
#include <cmath>
#include <functional>
#include <queue>
using namespace std;

typedef unsigned int uint;
typedef unsigned long long ull;

#define FORE(k,a,b) for(int k=(a); k <= (int)(b); ++k)
#define FOR(k,a,b) for(int k=(a); k < (int) (b); ++k)

#define pb push_back 
#define mp make_pair 

typedef vector<int> vi; 
typedef vector<bool> vb;
typedef vector<vb> vvb;
typedef vector<double> vd;
typedef vector<vi> vvi;
typedef vector<uint> uvi; 
typedef vector<uvi> uvvi;
typedef vector<vd> vvd;
typedef pair<int,int> ii;
typedef vector<pair<int,int> > vii;
typedef vector<vii> vvii;
typedef pair<uint,uint> uu;
typedef map<string, int> msi;
#define sz(a) int((a).size()) 
#define pb push_back 
#define all(c) (c).begin(),(c).end() 
#define FOR_IT(c,i) for(typeof((c).begin() i = (c).begin(); i != (c).end(); i++) 
#define contains(c,x) ((c).find(x) != (c).end()) 
#define cpresent(c,x) (find(all(c),x) != (c).end()) 

template<typename T>
bool isBetween(T x, T a, T b)
{
	return (a <= x && x <= b) ||
		(b <= x && x <= a);
}


template<typename T>
ostream& operator<<( ostream& os, const vector<T>& vec )
{
    FOR(i, 0, vec.size())
    {
        os <<  vec[i] << endl;
    }
    return os;
}

template< typename T >
struct Point
{
    T x;
    T y;
    
    Point(T xx, T yy) : x(xx), y(yy) {}
	Point() : x(), y() {}

	template < typename U >
    operator Point<U> (  ) const
    {
    	Point<U> result( (U) x, (U) y );
    	return result;
    }
};

template<typename T> 
Point<T> operator-( const Point<T>& lhs, const Point<T>& rhs) 
{
	Point<T> ret( lhs.x - rhs.x, lhs.y - rhs.y);
	return ret;
}

template<typename T> 
Point<T> operator+( const Point<T>& lhs, const Point<T>& rhs) 
{
	Point<T> ret( lhs.x + rhs.x, lhs.y + rhs.y);
	return ret;
}

template<typename T> 
bool operator==( const Point<T>& lhs, const Point<T>& rhs) 
{
	return lhs.x == rhs.x && lhs.y == rhs.y;
}

template<typename T> 
bool operator!=( const Point<T>& lhs, const Point<T>& rhs) 
{
	return lhs.x != rhs.x || lhs.y != rhs.y;
}

template<typename T> 
Point<T> operator/( const Point<T>& lhs, const T&  rhs) 
{
	Point<T> ret( lhs.x / rhs, lhs.y / rhs);
	return ret;
}

template<typename T> 
Point<T> operator*( const Point<T>& lhs, const T&  rhs) 
{
	Point<T> ret( lhs.x * rhs, lhs.y * rhs);
	return ret;
}


template<typename T> 
Point<T> operator*( const T&  lhs, const Point<T>& rhs) 
{
	return rhs * lhs;
}

/*
template<typename T> 
Point<T> operator*( int lhs, const Point<T>& rhs) 
{
	return rhs * lhs;
}*/

template<typename T> 
ostream& operator<<( ostream& os, const Point<T>& rhs) 
{
	os << "(" << rhs.x << ", " << rhs.y << ")" ;
	return os;
}

 


typedef Point<double> PointD;
typedef Point<int> PointI;
typedef pair<PointD, PointD> SegmentD;
typedef vector<PointD> vp;

template<typename T>
T dot( const Point<T>& A, const Point<T>& B )
{
    return A.x*B.x + A.y * B.y;       
}

template<typename T>
T mag( const Point<T>& v )
{
    return sqrt(v.x * v.x + v.y * v.y);
}

template<typename T>
T cross( const Point<T>& A, const Point<T>& B )
{
    return A.x*B.y - A.y*B.x;
}

const double tolerance = 0.000002;

//Rotate about origin counter clockwise
template<typename T>
Point<T> rotate90(const Point<T> & p)
{
    return Point<T>(-p.y, p.x);
}



/*
vector p1p2 ; p1p3
returns 
-1 if p1p3 is clockwise of p1p2 ( "right turn" )
+1 if p1p3 is counter-clockwise of p1p2 ( "left turn " )
0 if parallel
*/
template<typename T>
int  ccw(  const Point<T>& p1, 
    const Point<T>& p2, const Point<T>& p3)
{
    Point<T> v1 = p2 - p1;
    Point<T> v2 = p3 - p1;
    
    T cp = cross(v1, v2);
    
    //v2 is counter clockwise to v1, so we turned left
    if (cp > 0)
        return +1;
    
    //v2 is clockwise of v1, 
    if (cp < 0)
        return -1;
    
    //Vectors v1 and v2 are colinear
    return 0;
}

template<> 
int ccw<double>(  const Point<double>& p1, 
    const Point<double>& p2, const Point<double>& p3)
{
    Point<double> v1 = p2 - p1;
    Point<double> v2 = p3 - p1;
    
    double cp = cross(v1, v2);
    
    //Vectors v1 and v2 are colinear
    if (abs(cp) < tolerance) {
        return 0;
    }
    
    //v2 is counter clockwise to v1, so we turned left
    if (cp > 0)
        return +1;
    
    //v2 is clockwise of v1, 
    if (cp < 0)
        return -1;
    
	int up = 3;
    throw up; 
}

template<typename T> T gcd(T a, T b)
{
	if (!numeric_limits<T>::is_exact)
		return min( abs(a), abs(b) );

    if (a == 0)
       return b;
    while (b != 0)
	{
        if (a > b)
           a = a - b;
        else
           b = b - a;
	}
    return a;
}

template<class T>
class Line
{
	public:
	//ax + by + c = 0
	T A;
	T B;
	T C;

	Line(const Point<T>& p1, const Point<T>& p2)
	{
			
	    assert(p1 != p2);
		//cout << "Building line" << endl;
		A = p1.y - p2.y;
		B = p2.x - p1.x;
		C = p1.x*p2.y - p2.x * p1.y;

		//make A positive
		if (A < 0 || (A==0 && B < 0) )
		{
			A *= -1;
			B *= -1;
			C *= -1;
		}

		T gcdAB = gcd( abs(A), abs(B) );
		T gcdABC = gcd( gcdAB, abs(C) );

		A /= gcdABC;
		B /= gcdABC;
		C /= gcdABC;

		//assert(A * p1.x + B * p1.y + C == 0);
		//assert(A * p2.x + B * p2.y + C == 0);
	}
};

template<class T>
class Segment
{
	public:
	//ax + by + c = 0
	Point<T> p1;
	Point<T> p2;

	Segment(const Point<T>& pp1 = Point<T>(0,0), const Point<T>& pp2 = Point<T>(0,0))
		: p1(pp1), p2(pp2)
	{
			
	    
	}
};

template<typename T> 
ostream& operator<<( ostream& os, const Segment<T>& rhs) 
{
	os << " " << rhs.p1 << " -- " << rhs.p2 << " " ;
	return os;
}

template<typename T> 
ostream& operator<<( ostream& os, const Line<T>& rhs) 
{
	os << " " << rhs.A << "x + " << rhs.B << " y + " << rhs.C << " = 0" ;
	return os;
}

template<typename T> 
bool operator<( const Line<T>& lhs, const Line<T>& rhs) 
{
	if (lhs.A != rhs.A)
		return lhs.A < rhs.A;

	if (lhs.B != rhs.B)
		return lhs.B < rhs.B;

	return lhs.C < rhs.C;
}

template<typename T> 
bool operator==( const Line<T>& lhs, const Line<T>& rhs) 
{
	return (lhs.A == rhs.A) &&
		
	 (lhs.B == rhs.B) &&
		 lhs.C == rhs.C;
}

template<typename T>
class Circle
{
public:
	T x;
	T y;
	T r;

	Circle(T _x, T _y, T _r) : x(_x), y(_y), r(_r)
	{

	}

	int sgn(double x) {
        if (x < 0)
            return -1;
        return 1;
    }

	bool getPointsIntersectingLine(const Point<T>& p1,
			const Point<T>& p2, Point<double>& i1,  Point<double>& i2 )
	{
        //Move circle to origin
        T x2 = p2.x - x;
        T y2 = p2.y - y;
        T x1 =  p1.x - x;
        T y1 =  p2.y - y;
        T dx = x2-x1;
        T dy = y2-y1;
        double dr = sqrt(dx*dx+dy*dy);
        T D = x1*y2-x2*y1;

        double disc =r*r*dr*dr-D*D;
        if (disc < 0)
            return false;
        
        double discSqRt = sqrt(disc);
        i1.x = x+ (D*dy+sgn(dy)*dx*discSqRt) / (dr*dr);
        i1.y = y+ (-D*dx+abs(dy) * discSqRt) / (dr*dr);

        i2.x = x+ (D*dy-sgn(dy)*dx*discSqRt) / (dr*dr);
        i2.y = y+ (-D*dx-abs(dy) * discSqRt) / (dr*dr);

        return true;
    }
};

template<typename T>
bool isParallel( const Point<T>& a1, const Point<T>& a2, 
				const Point<T>& b1, const Point<T>& b2)
{
	T z = cross(a2 - a1, b2 - b1 );

	if (numeric_limits<T>::is_exact && z == 0)
		return true;

	if (!numeric_limits<T>::is_exact && abs(z) < tolerance)
		return true;

	return false;
}

template<typename T>
int getSide( const Point<T>& A, const Point<T>& B, const Point<T>& P)
{
    T z = cross(B-A, P-A);
    
	if (numeric_limits<T>::is_exact) {
		if (z > 0) {
#ifndef ONLINE_JUDGE
			cout << "Points " << A << " " << B << " " << P << " 1 " << endl;
#endif
			return 1;
		}
		if (z < 0) {
#ifndef ONLINE_JUDGE
			cout << "Points " << A << " " << B << " " << P << " -1 " << endl;
#endif
			return -1;
		}
	}
	else {
		if (z - tolerance > 0)
			return 1; 
		if (z + tolerance < 0)
			return -1; 
	}

#ifndef ONLINE_JUDGE
	cout << "Points " << A << " " << B << " " << P << " 0 " << endl;
#endif
    return 0; 
}

template<typename T>
bool isColinear(  const Point<T>& p1, 
    const Point<T>& p2, const Point<T>& p3)
{
	if (p1 == p2 || p2 == p3)
		return true;

    return 0 == getSide(p1, p2, p3);
}

template<typename T>
bool intersects(const Point<T>& a1,const Point<T>& a2,
				const Point<T>& b1,const Point<T>& b2)
{
	if (a1==a2)
	{ 
		return isBetween(a1.x, b1.x, b2.x) &&
			isBetween(a1.y, b1.y, b2.y);
	}
	
	if ( getSide( a1, a2, b1 ) == getSide( a1, a2, b2 ) )
		return false; 

      
	if (b1==b2)
	{
		return isBetween(b1.x, a1.x, a2.x) &&
			isBetween(b1.y, a1.y, a2.y);
	}

    if ( getSide( b1, b2, a1) == getSide( b1, b2, a2 ) )
	    return false;
	
    return true;
}

//http://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
template<typename T>
bool getIntersection(const Point<T>& a1,const Point<T>& a2,
				const Point<T>& b1,const Point<T>& b2, PointD& inter)
{
    Point<double> p = (PointD) a1;
    Point<double> r = a2 - a1;
    
    PointD q = b1;
    PointD s = b2 - b1;
    
    //P + t*r intersects q + u*s
    
    double rCrossS = cross(r, s);
    
    if ( abs( rCrossS ) < tolerance )
        return false;
    
    double t = cross( q-p,  s / rCrossS);
    
    inter = p + t*r;
    
	if (t+tolerance < 0 || t-tolerance > 1)
		return false;
   
	double u = cross(q-p, r / rCrossS);
    
    if (u+tolerance < 0 || u-tolerance > 1)
		return false;

    return true;
}

double dist( const PointD& p1, const PointD& p2 )
{
	return sqrt( (p1.x-p2.x)*(p1.x-p2.x) + (p1.y-p2.y)*(p1.y-p2.y) );
}


bool cmp(const PointI& i, const PointI& j) {
	return (i.x != j.x)  ?
		i.x < j.x :
		i.y < j.y;
}

typedef vector<PointI> vecP ;

bool cmpLine(const vecP& v1, const vecP& v2)
{
	uint i = 0;
	while( i < v1.size() && i < v2.size() )
	{
		if (v1[i] != v2[i])
		{
			return cmp(v1[i], v2[i]);
		}
		++i;
	}

	return v1.size() < v2.size();
}

template<typename A, typename B>
void minPair( pair<A,B>& minPair, const pair<A,B>& pair)
{
	minPair.first = min(minPair.first, pair.first);
	minPair.second = min(minPair.second, pair.second);
}

template<typename A, typename B>
void maxPair( pair<A,B>& maxPair, const pair<A,B>& pair)
{
	maxPair.first = max(maxPair.first, pair.first);
	maxPair.second = max(maxPair.second, pair.second);
}

int cmpYX(const PointI& a, const PointI& b)
{
    if (a.y != b.y)
    {
        return a.y < b.y;
    }
    return a.x < b.x;
}

template<typename T>
struct PolarCmp
{
    Point<T> origin;
    
    PolarCmp( const Point<T> ori ) : origin(ori) 
    {
    }
    
    int operator()(const Point<T>& a, const Point<T>& b)
    {
       int isCCW = ccw(origin, a, b);
       
       if (isCCW == 1) 
           return true;
       
       return false;
    }
};

template<typename T>
double polyArea ( const vector<Point<T> >& points) 
{
        double sum = 0;
        for(int i = 0; i < points.size() - 1; ++i) {            
            sum += points[i].x * points[i+1].y;  
        }
        
        sum += points[ points.size()-1].x * points[0].y;
        
        for(int i = 0; i < points.size() - 1; ++i) {            
            sum -= points[i+1].x * points[i].y;  
        }
        
        sum -= points[0].x * points[points.size()-1].y;
        
        return abs(sum) / 2;
}


int main() {
#ifndef ONLINE_JUDGE
	freopen ("input.txt","r",stdin);
	//freopen ("in.txt","r",stdin);
#endif

	while(true)
	{
		vector<PointD> points;
		vector<PointD> triangle;
		FOR(i, 0, 6)
		{
			PointD pt;
			cin >> pt.x >> pt.y;
			if (i >= 3)
				triangle.pb(pt);
			else 
				points.pb(pt);
		}
		

		if (isColinear(triangle[0], triangle[1], triangle[2]))
			break;

		double tArea = polyArea(triangle);

		//cout << tArea << endl;

		PointD a = points[0];
		PointD b = points[1];
		PointD c = points[2];

		double AB = dist(a, b);

		double height = tArea / AB;
		
		double angABC = acos( dot(c-a, b-a) / ( mag(c-a) * mag(b-a) ) );
		
		//sin ang = height / hype
		// hyp = height / sin ang
		double AH = height / sin(angABC);

		//Find unit vector AC

		PointD vecAC = c - a;
		double AC = dist(points[0], points[2]);
		vecAC.x /= AC;
		vecAC.y /= AC;

		PointD h = points[0] + vecAC * AH;
		PointD g = points[1] + vecAC * AH;

		printf("%.3f %.3f %.3f %.3f\n",
			g.x, g.y, h.x, h.y);
			
			

		
#if 0
		cout << setw(3) << g << h << endl;
		Line<double> lineAH(a, h);
		Line<double> lineAC(a, c);

		cout << "Vec AC dist " << dist(PointD(0,0), vecAC) << endl;
		

		cout << "Line AH" << lineAH << endl;
		cout << "Line AC" << lineAC << endl;

		cout << dist(points[0], h) << endl;
		cout << dist(points[1], g) << endl;
		cout << "Trianle area " << tArea << endl;
		cout << "\\ \\ area " << dist(a, h) * AB << endl;
		cout << " || " << isParallel(a, h, b, g) << endl;
		cout << " || " << isParallel(g, h, b, a) << endl;
		cout << isColinear(a, h, c) << endl;
#endif
	}
	return 0;
}

