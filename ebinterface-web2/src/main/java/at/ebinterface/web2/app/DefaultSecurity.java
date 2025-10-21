package at.ebinterface.web2.app;

import com.helger.annotation.concurrent.Immutable;
import com.helger.photon.security.CSecurity;
import com.helger.photon.security.mgr.PhotonSecurityManager;
import com.helger.photon.security.role.IRoleManager;
import com.helger.photon.security.user.IUserManager;
import com.helger.photon.security.usergroup.IUserGroupManager;

@Immutable
public final class DefaultSecurity
{
  private DefaultSecurity ()
  {}

  public static void init ()
  {
    final IUserManager aUserMgr = PhotonSecurityManager.getUserMgr ();
    final IUserGroupManager aUserGroupMgr = PhotonSecurityManager.getUserGroupMgr ();
    final IRoleManager aRoleMgr = PhotonSecurityManager.getRoleMgr ();

    // Standard users
    if (!aUserMgr.containsWithID (CSecurity.USER_ADMINISTRATOR_ID))
      aUserMgr.createPredefinedUser (CSecurity.USER_ADMINISTRATOR_ID,
                                     CSecurity.USER_ADMINISTRATOR_EMAIL,
                                     CSecurity.USER_ADMINISTRATOR_EMAIL,
                                     CSecurity.USER_ADMINISTRATOR_PASSWORD,
                                     CSecurity.USER_ADMINISTRATOR_NAME,
                                     null,
                                     null,
                                     null,
                                     null,
                                     false);

    // Create all roles
    if (!aRoleMgr.containsWithID (CAppSecurity.ROLEID_CONFIG))
      aRoleMgr.createPredefinedRole (CAppSecurity.ROLEID_CONFIG, "Config user", null, null);

    // User group Administrators
    if (!aUserGroupMgr.containsWithID (CAppSecurity.USERGROUPID_SUPERUSER))
    {
      aUserGroupMgr.createPredefinedUserGroup (CAppSecurity.USERGROUPID_SUPERUSER, "Administrators", null, null);
      // Assign administrator user to UG administrators
      aUserGroupMgr.assignUserToUserGroup (CSecurity.USERGROUP_ADMINISTRATORS_ID, CSecurity.USER_ADMINISTRATOR_ID);
    }
    aUserGroupMgr.assignRoleToUserGroup (CSecurity.USERGROUP_ADMINISTRATORS_ID, CAppSecurity.ROLEID_CONFIG);

    // User group Config users
    if (!aUserGroupMgr.containsWithID (CAppSecurity.USERGROUPID_CONFIG))
      aUserGroupMgr.createPredefinedUserGroup (CAppSecurity.USERGROUPID_CONFIG, "Config user", null, null);
    aUserGroupMgr.assignRoleToUserGroup (CAppSecurity.USERGROUPID_CONFIG, CAppSecurity.ROLEID_CONFIG);
  }
}
