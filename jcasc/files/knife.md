Knife file should include the required files as environment variables, they will be included at runtime.
```ruby
log_level                :info
log_location             STDOUT
node_name                'pivotal'
client_key               (ENV['PRIVATEPEM']).to_s
chef_server_url          'https://api.chef.io/organizations/default'
# ssl_ca_file              "#{ENV['OPSWORKSPEM']}"
```
