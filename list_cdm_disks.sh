
#!/bin/bash

MAX_DISK_TOTAL="0"
WARN_MB="4"
ERROR_MB="2"

DOMAIN="UIMDOMAIN"
THISDIR="${0%/*}"
NM_ROOT="/opt/nimsoft"
PATH="$NM_ROOT/bin:$PATH"
U="administrator"
P="*********"

cd "$THISDIR"

LOOK_FOR_DISKS="
C:
D:
E:
F:
/
/var
/opt
/tmp
/var/log
/home
/usr
/usr/local
"

for HUB in $(./list_hubs.sh)
do
        ROBOTS=$(./list_robots.sh "$HUB")

        echo "$ROBOTS"| while read ROBOT_NAME
        do

                ROBOT_ADDRESS="$DOMAIN/$HUB/$ROBOT_NAME"
                CDM_ADDRESS="/$DOMAIN/$HUB/$ROBOT_NAME/cdm"
                CONTROLLER_ADDRESS="/$DOMAIN/$HUB/$ROBOT_NAME/controller"

                echo
                echo "$CDM_ADDRESS"

                #echo pu -u "$U" -p "$P" "$CDM_ADDRESS" "$@"
                DISKS=$(pu -u "$U" -p "$P" "$CDM_ADDRESS" disk_status "" "" | sed -e "s/\\\//g")
                RC="$?"
                if [ "$RC" != 0 ]
                then

                        echo "WARN pu command failure ($RC): $DISKS" >&2
                        continue
                fi

                I=0
                echo "$DISKS" | grep -A11 FileSys |
                while read KEY DATATYPE SIZE VALUE
                do
                        #echo "$I $KEY='$VALUE'"
                        (( I+= 1 ))

                        case "$KEY" in

                        DiskActive) #='1'
                                DISK_ACTIVE="$VALUE"
                                # skip if the disk is not active
                                if [ "$DISK_ACTIVE" != "1" ]
                                then
                                        break
                                fi
                                ;;
                        DiskTotal) #='61087'
                                DISK_TOTAL="$VALUE"
                                ;;
                        DiskFree) #='48022'
                                DISK_FREE="$VALUE"
                                ;;
                        DiskAvail) #='48022'
                                DISK_AVAIL="$VALUE"
                                ;;
                        DiskUsed) #='13065'
                                DISK_USED="$VALUE"
                                ;;
                        TypeDesc)
                                # skip if the disk is not local
                                if [ "$VALUE" != "Local" ]
                                then
                                        break
                                fi
                                ;;
                        FileSys)
                                FILESYS="$VALUE"
                                if [ "$SIZE" -gt 18 ]
                                then
                                        echo "WARN: Unable to configure $CDM_ADDRESS $KEY $VALUE is too long, and may be truncated." >&2
                                        break
                                fi

                                # see if we care about this one
                                FOUND=$(echo "$LOOK_FOR_DISKS" | grep "^$FILESYS")
                                if [ -z "$FOUND" ]
                                then
                                        break
                                fi
                                ;;

                        DiskUsedPct) #='21'
                                DISK_USED_PCNT="$VALUE"
                                # this is the last piece of data we need to have all of the information for this disk

                                if [ "$DISK_TOTAL" -gt "$MAX_DISK_TOTAL" ]
                                then
                                        # this disk is big enough, make sure it is configured to be monitored in MB, not %

                                        echo -e "$ROBOT\tDisk $FILESYS total=$DISK_TOTAL, free=$DISK_FREE"

                                        PERCENT=$(pu -u "$U" -p "$P" "$CONTROLLER_ADDRESS" probe_config_get cdm "$ROBOT" "$CONFIG_BASE/percent" | grep "^value" | awk '{print $4}')
                                        ERROR_THRESH=$(pu -u "$U" -p "$P" "$CONTROLLER_ADDRESS" probe_config_get cdm "$ROBOT" "$CONFIG_BASE/error/threshold" | grep "^value"| awk '{print $4}')
                                        WARN_THRESH=$(pu -u "$U" -p "$P" "$CONTROLLER_ADDRESS" probe_config_get cdm "$ROBOT" "$CONFIG_BASE/warning/threshold" | grep "^value"| awk '{print $4}')


                                fi


                                ;;
                        esac



                done

        done
break #temporary bail after first hub

done
