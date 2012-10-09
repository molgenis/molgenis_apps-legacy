DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
touch $DIR/workflow_csv.started

#s00_GuestInvitation_1
s00_GuestInvitation_1=$(qsub -N s00_GuestInvitation_1 s00_GuestInvitation_1.sh)
echo $s00_GuestInvitation_1
sleep 8
#s00_GuestInvitation_2
s00_GuestInvitation_2=$(qsub -N s00_GuestInvitation_2 s00_GuestInvitation_2.sh)
echo $s00_GuestInvitation_2
sleep 8
#s00_GuestInvitation_3
s00_GuestInvitation_3=$(qsub -N s00_GuestInvitation_3 s00_GuestInvitation_3.sh)
echo $s00_GuestInvitation_3
sleep 8
#s00_GuestInvitation_4
s00_GuestInvitation_4=$(qsub -N s00_GuestInvitation_4 s00_GuestInvitation_4.sh)
echo $s00_GuestInvitation_4
sleep 8
#s00_GuestInvitation_5
s00_GuestInvitation_5=$(qsub -N s00_GuestInvitation_5 s00_GuestInvitation_5.sh)
echo $s00_GuestInvitation_5
sleep 8
#s01_OrganizerInvitation_1
s01_OrganizerInvitation_1=$(qsub -N s01_OrganizerInvitation_1 -W depend=afterok:$s00_GuestInvitation_Cindy:$s00_GuestInvitation_Charly s01_OrganizerInvitation_1.sh)
echo $s01_OrganizerInvitation_1
sleep 8
#s01_OrganizerInvitation_2
s01_OrganizerInvitation_2=$(qsub -N s01_OrganizerInvitation_2 -W depend=afterok:$s00_GuestInvitation_Adri:$s00_GuestInvitation_Abel:$s00_GuestInvitation_Adam s01_OrganizerInvitation_2.sh)
echo $s01_OrganizerInvitation_2
sleep 8
