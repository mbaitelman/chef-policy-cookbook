name 'server'

default_source :community
default_source :chef_repo, '../..'

run_list(
    'recipe[chef_client_updater]',
    'recipe[chef-client]',
    'recipe[chef-policy-cookbook::nginx_recipe]'
  )

default['chef_client']['splay'] = 250
default['chef_client']['interval'] = 2000
