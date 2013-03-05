## Makefile.am -- Process this file with automake to produce Makefile.in
ACLOCAL_AMFLAGS=-I m4
AM_CFLAGS = -Wall -m64 -fPIC
bin_PROGRAMS = bwa
noinst_LIBRARIES = libbwacore.a
libbwacore_a_SOURCES = utils.c bwt.c bwtio.c bwtaln.c bwtgap.c bntseq.c \
	stdaln.c bwaseqio.c bwase.c kstring.c cs2nt.c 
bwa_SOURCES = is.c bwtmisc.c bwtindex.c simple_dp.c bwape.c \
	bwtsw2_core.c bwtsw2_main.c bwtsw2_aux.c bwt_lite.c bwtsw2_chain.c \
	main.c
bwa_LDADD = bwt_gen/libbwtgen.a libbwacore.a
man_MANS= bwa.1
SUBDIRS= bwt_gen .
