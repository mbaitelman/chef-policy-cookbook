control 'user-root' do
  impact 1.0

  describe user('root') do
    it { should exist }
    its('group') { should eq 'root' }
    its('groups') { should eq %w(root) }
    its('home') { should eq '/root' }
    its('shell') { should eq '/bin/bash' }
  end
end

control 'nginx-setup' do
  impact 1.0

  describe http('http://localhost') do
    its('status') { should cmp 200 }
    its('body') { should cmp 'Hello Chef Friends!' }
    # its('headers.Content-Type') { should cmp 'text/html' }
  end
end
