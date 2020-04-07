control 'user-root' do
  impact 1.0

  describe user('root') do
    it { should exist }
    its('group') { should eq 'root' }
    its('groups') { should eq %w(root other) }
    its('home') { should eq '/root' }
    its('shell') { should eq '/bin/bash' }
  end
end
