name 'server'

default_source :community

run_list(
    'recipe[chef_client_updater]',
    'recipe[chef-client]',
    'recipe[ntp]'
  )

default['chef_client']['splay'] = 250
default['chef_client']['interval'] = 2000
