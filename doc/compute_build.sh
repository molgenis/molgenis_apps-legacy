#name of the directory and the main file in that directory
PREFIX=compute

#build directory
mkdir ../dist/doc
OUTDIR="../dist/doc/$PREFIX"
mkdir $OUTDIR
cp -R $PREFIX/* $OUTDIR/

#change to outdir
cd $OUTDIR

#html
pandoc -s -S --toc -c pandoc.css *.md -o $PREFIX.html

#pdf
#pandoc -s -S --toc -c pandoc.css *.md -o $PREFIX.pdf

#texi
#pandoc *.md -s -o $PREFIX.texi
#makeinfo *.texi --html -o web
#texi2pdf *.texi 
