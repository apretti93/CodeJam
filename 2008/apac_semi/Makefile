CC = g++
CFLAGS =  -O3
OLD = -g -O3 -Wall
EXEC_NAME = millionaire.exe apocalypse_soon.exe
OLD_EXEC_NAME = what_are_birds.exe 
INCLUDES = -IC:\codejam\boost_1_52_0
LIBS =
OBJ_FILES = star_wars.o simplex.o util.o
INSTALL_DIR = /usr/bin

ifeq ($(mode),release)
   CFLAGS = -O3
   CFLAGS +=-DCLEANTLOG
else
   mode = debug
   CFLAGS = -g -Wall 
   CFLAGS+=-DTLOG
endif

# C++ compile flags
#CFLAGS+=-DFTLOG
#CFLAGS+=-DFTLOG="${PROG}"
#CFLAGS+=-DETLOG

#


CXX_FILES = $(wildcard *.cxx)

all : $(EXEC_NAME)


#.depend: depend



depend: $(CXX_FILES)
				rm -f ./.depend
				$(CC) $(CFLAGS) -MM $^>>./.depend;

include .depend
				
clean :
	rm $(EXEC_NAME) $(OBJ_FILES)

%.exe : %.o util.o tri_logger.o
	$(CC) $(CFLAGS) -o $@ $< util.o tri_logger.o $(LIBS)
	
test_util.exe : util_test.o util.o
	$(CC) -static -o $@ $< util.o -lboost_unit_test_framework
	
graph_test.exe : graph_test.o graph.h graph_test.cxx
	$(CC) -static -o $@ $< util.o tri_logger.o -lboost_unit_test_framework
	
%.o: %.cxx
	$(CC) $(CFLAGS) $(INCLUDES) -o $@ -c $<


