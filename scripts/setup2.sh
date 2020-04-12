
# Update artifactory 
curl -uadmin:password -X PATCH "http://localhost:8081/artifactory/api/system/configuration" -H "Content-Type:application/yaml" -T artifactory/configuration.yml
