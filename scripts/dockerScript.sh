env_keys=("ACTIVE_PROFILE" "DEV_DBNAME" "DEV_USERNAME" "DEV_PASSWORD" "GOOGLE_CLIENT_ID" "GOOGLE_CLIENT_SECRET" "GOOGLE_REDIRECT_URL" "GIT_CLIENT_ID" "GIT_CLIENT_SECRET" "GIT_REDIRECT_URI" "NORMAL_SECRET_KEY" "NORMAL_PUBLIC_KEY" "NORMAL_PRIVATE_KEY")
touch env_vars.list
printenv | \
 while read -r line; do
   key=$(echo "$line" | cut -d "=" -f1)
   value=$(echo "$line" | cut -d "=" -f2)
   if [[ "${env_keys[*]}" =~ $key ]]; then
     echo "match"
    printf "%s=%s\n" "$key" "$value" >> env_vars.list
   fi
  done
cat env_vars.list
chmod 700 env_vars.list

PASS=$(aws ecr get-login-password --region us-east-2)
echo "$PASS" | docker login -u AWS --password-stdin  https://859198873575.dkr.ecr.us-east-2.amazonaws.com
docker pull 859198873575.dkr.ecr.us-east-2.amazonaws.com/recipes-app-backend:latest
CONTAINER_ID=$(docker run -d -p 80:8080 --env-file env_vars.list 859198873575.dkr.ecr.us-east-2.amazonaws.com/recipes-app-backend:latest)
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
