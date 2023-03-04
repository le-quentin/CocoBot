docker_access_token=$CR_PAT
image_name=ghcr.io/le-quentin/cocobot
version_tag=`git rev-parse --short HEAD`

if [ -z ${docker_access_token} ]; then
  >&2 echo 'CR_PAT env var needs to be set to the GitHub repo access token'; exit 1
fi

if ! ./gradlew build; then
  >&2 echo 'Could not build project'; exit 1
fi

if ! echo $docker_access_token | docker login ghcr.io -u le-quentin --password-stdin; then
  >&2 echo 'Could not login to docker registry'; exit 1
fi

docker buildx build \
--push \
--platform linux/amd64 \
--platform linux/arm/v7 \
--tag "$image_name":"sha-$version_tag" \
--tag "$image_name":latest \
.