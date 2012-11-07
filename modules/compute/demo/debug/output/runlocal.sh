DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
touch $DIR/workflow_csv.started
export PBS_O_WORKDIR=${DIR}
echo Starting with s00_GuestInvitationStep_1...
sh s00_GuestInvitationStep_1.sh
#Dependencies: 

echo Starting with s00_GuestInvitationStep_2...
sh s00_GuestInvitationStep_2.sh
#Dependencies: 

echo Starting with s00_GuestInvitationStep_3...
sh s00_GuestInvitationStep_3.sh
#Dependencies: 

echo Starting with s00_GuestInvitationStep_4...
sh s00_GuestInvitationStep_4.sh
#Dependencies: 

echo Starting with s00_GuestInvitationStep_5...
sh s00_GuestInvitationStep_5.sh
#Dependencies: 

echo Starting with s01_OrganizerInvitationStep_child...
sh s01_OrganizerInvitationStep_child.sh
#Dependencies: -W depend=afterok:$s00_GuestInvitationStep_1:$s00_GuestInvitationStep_2

echo Starting with s01_OrganizerInvitationStep_adult...
sh s01_OrganizerInvitationStep_adult.sh
#Dependencies: -W depend=afterok:$s00_GuestInvitationStep_3:$s00_GuestInvitationStep_4:$s00_GuestInvitationStep_5

