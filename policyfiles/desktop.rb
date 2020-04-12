name 'desktop'

default_source :community

# Deps

run_list(
    'recipe[chef_client_updater]',
    'recipe[chef-client]'
  )

default['chef_client']['splay'] = 350
