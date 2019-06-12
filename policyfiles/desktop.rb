name 'desktop'

default_source :community

run_list(
    'recipe[chef_client_updater]',
    'recipe[chef-client]',
    'recipe[ntp]',
)

default['chef_client']['splay'] = 350