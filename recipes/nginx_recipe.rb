
nginx_install 'default' do
  source 'distro'
end

nginx_site 'default' do
  template 'nginx-site.erb'
  cookbook 'chef-policy-cookbook'
  #    variables Hash
end

directory '/opt/server/' do
  recursive true
end

file '/opt/server/index.html' do
  content 'Hello Chef Friends!'
  owner 'root'
  group 'root'
  mode '0755'
  action :create
end
