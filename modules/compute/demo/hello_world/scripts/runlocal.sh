DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
touch $DIR/workflow_csv.started
export PBS_O_WORKDIR=${DIR}
echo Starting with s00_GuestInvitation_1...
sh s00_GuestInvitation_1.sh
#Dependencies: 

echo Starting with s00_GuestInvitation_2...
sh s00_GuestInvitation_2.sh
#Dependencies: 

echo Starting with s00_GuestInvitation_3...
sh s00_GuestInvitation_3.sh
#Dependencies: 

echo Starting with s00_GuestInvitation_4...
sh s00_GuestInvitation_4.sh
#Dependencies: 

echo Starting with s00_GuestInvitation_5...
sh s00_GuestInvitation_5.sh
#Dependencies: 

echo Starting with s01_OrganizerInvitation_1...
sh s01_OrganizerInvitation_1.sh
#Dependencies: -W depend=afterok:$s00_GuestInvitation_Cindy:$s00_GuestInvitation_Charly

echo Starting with s01_OrganizerInvitation_2...
sh s01_OrganizerInvitation_2.sh
#Dependencies: -W depend=afterok:$s00_GuestInvitation_Adri:$s00_GuestInvitation_Abel:$s00_GuestInvitation_Adam

