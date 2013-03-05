#!/usr/bin/perl -w

use strict;
use warnings;

die("Usage: sracsfq2fq.pl <in.sra.fasta>\n") if (@ARGV == 0 && -t STDIN);
while (<>) {
  print;
  $_ = <>; $_ = substr($_, 2);
  tr/0123./ACGTN/;
  print;
  $_ = <>; print $_;
  $_ = <>; print substr($_, 2);
}
