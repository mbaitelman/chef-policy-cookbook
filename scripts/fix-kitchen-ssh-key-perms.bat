:: Set Variable ::
set key="C:/workspace/chef-policy-cookbook/.kitchen/default-ubuntu-1804.pem"

:: Remove Inheritance ::
cmd /c icacls %key% /c /t /inheritance:d

:: Remove All Users, except for Owner ::
cmd /c icacls %key%  /c /t /remove Administrator "Authenticated Users" BUILTIN\Administrators BUILTIN Everyone System Users

:: Set Ownership to Owner ::
cmd /c icacls %key% /c /t /grant %username%:F

:: Verify ::
cmd /c icacls %key%