DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
touch $DIR/workflow_csv.started

#s00_GuestInvitationStep_1
s00_GuestInvitationStep_1=$(qsub -N s00_GuestInvitationStep_1 s00_GuestInvitationStep_1.sh)
echo $s00_GuestInvitationStep_1
sleep 0
#s00_GuestInvitationStep_2
s00_GuestInvitationStep_2=$(qsub -N s00_GuestInvitationStep_2 s00_GuestInvitationStep_2.sh)
echo $s00_GuestInvitationStep_2
sleep 0
#s00_GuestInvitationStep_3
s00_GuestInvitationStep_3=$(qsub -N s00_GuestInvitationStep_3 s00_GuestInvitationStep_3.sh)
echo $s00_GuestInvitationStep_3
sleep 0
#s00_GuestInvitationStep_4
s00_GuestInvitationStep_4=$(qsub -N s00_GuestInvitationStep_4 s00_GuestInvitationStep_4.sh)
echo $s00_GuestInvitationStep_4
sleep 0
#s00_GuestInvitationStep_5
s00_GuestInvitationStep_5=$(qsub -N s00_GuestInvitationStep_5 s00_GuestInvitationStep_5.sh)
echo $s00_GuestInvitationStep_5
sleep 0
#s01_OrganizerInvitationStep_child
s01_OrganizerInvitationStep_child=$(qsub -N s01_OrganizerInvitationStep_child -W depend=afterok:$s00_GuestInvitationStep_1:$s00_GuestInvitationStep_2 s01_OrganizerInvitationStep_child.sh)
echo $s01_OrganizerInvitationStep_child
sleep 0
#s01_OrganizerInvitationStep_adult
s01_OrganizerInvitationStep_adult=$(qsub -N s01_OrganizerInvitationStep_adult -W depend=afterok:$s00_GuestInvitationStep_3:$s00_GuestInvitationStep_4:$s00_GuestInvitationStep_5 s01_OrganizerInvitationStep_adult.sh)
echo $s01_OrganizerInvitationStep_adult
sleep 0
#s02_CheckResults_constant
s02_CheckResults_constant=$(qsub -N s02_CheckResults_constant -W depend=afterok:$s01_OrganizerInvitationStep_child:$s01_OrganizerInvitationStep_adult s02_CheckResults_constant.sh)
echo $s02_CheckResults_constant
sleep 0

touch $DIR/workflow_csv.finished
