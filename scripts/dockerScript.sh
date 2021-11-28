env_keys=("ACTIVE_PROFILE" "DEV_DBNAME" "DEV_USERNAME" "DEV_PASSWORD" "GOOGLE_CLIENT_ID" "GOOGLE_CLIENT_SECRET" "GOOGLE_REDIRECT_URL" "GIT_CLIENT_ID" "GIT_CLIENT_SECRET" "GIT_REDIRECT_URI" "NORMAL_SECRET_KEY" "NORMAL_PUBLIC_KEY" "NORMAL_PRIVATE_KEY")
touch env_vars.list
cp /dev/null env_vars.list
compareString=" ${env_keys[*]} "
compareString=${compareString//" "/"|"}
echo "$compareString"
printenv | \
 while read -r line; do
   key=$(echo "$line" | cut -d "=" -f1)
   manipulatedKey="|$key|"
   echo "$manipulatedKey"
   value=$(echo "$line" | cut -d "=" -f2)
   if [[ "${compareString}" =~ "${manipulatedKey}" ]]; then
     printf "%s=%s\n" "$key" "$value" >> env_vars.list
   fi
  done
chmod 700 env_vars.list

PASS=$DOCKER_HUB_PASSWORD
echo "$PASS" | docker login --username=${DOCKER_HUB_USERNAME} --password-stdin
docker pull ${DOCKER_HUB_USERNAME}/recipes-app-backend:latest
CONTAINER_ID=$(docker run -d -p 80:8080 --env-file env_vars.list ${DOCKER_HUB_USERNAME}/recipes-app-backend:latest)
echo "Please wait until docker health check is complete"
sleep 10
if ! docker top "$CONTAINER_ID" &>/dev/null
then
        echo "There was an error"
        exit 1
else
        echo "Successful"
fi
exit 0
