sudo docker rm -f $(sudo docker ps -aq)
rm -r channel-artifacts/*
rm -r crypto-config
