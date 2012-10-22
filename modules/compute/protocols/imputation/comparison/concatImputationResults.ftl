#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

getFile ${impute2ResultsBinsLocation}
getFile ${tooldir}/python_scripts/AssemblyImpute2GprobsBins.py

getFile srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/generated/2012-02-23/file55799619-5aab-4b6b-9966-25afa29ed537
getFile srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/generated/2012-02-24/file908de01b-247c-43d1-8f4c-ba4ceaa58baa
getFile srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/generated/2012-02-24/file98118c02-3636-4146-9090-5f9044481f23
getFile srm://carme.htc.biggrid.nl/dpm/htc.biggrid.nl/home/bbmri.nl/generated/2012-02-24/file61914e6b-ad7d-402b-a4eb-87345b1a257b
getFile srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/generated/2012-02-25/file98256d5e-d944-4b32-bbfd-a82879d1b1f3
getFile srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/generated/2012-02-25/filed1b778e4-34bf-4b07-8090-718f640b3719
getFile srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/generated/2012-02-25/file25ebd12c-fd30-4da2-9ac7-ec5ccfb3903d
getFile srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/generated/2012-02-25/filef64b15ca-cc2c-4a26-84b4-22a0e12d5e59
getFile srm://gb-se-ams.els.sara.nl/dpm/els.sara.nl/home/bbmri.nl/generated/2012-03-01/fileece91ee3-ddb7-476e-8e56-bd6ab5d7f113
getFile srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/bbmri.nl/generated/2012-02-25/file919db34c-1529-4505-9b3e-723cb9247fd0

inputs "${impute2ResultsBinsLocation}"

mkdir -p ${resultsDir}
python ${tooldir}/python_scripts/AssemblyImpute2GprobsBins.py ${impute2ResultsBinsLocation} 500 ${chr} ${resultsDir}/OUTPUT.gprobs

putFile ${resultsDir}/OUTPUT.gprobs

