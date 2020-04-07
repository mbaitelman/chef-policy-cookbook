
# Update artifactory 
curl -uadmin:password -X PATCH "http://localhost:8082/artifactory/api/system/configuration" -H “Content-Type:application/yaml” -T artifactory/configuration.yml
