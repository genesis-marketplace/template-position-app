package scripts

dynamicPermissions {
  entity(USER_EID) {
    name = "EID_VISIBILITY"
    maxEntries = 10000
    batchingPeriod = 15
    idField = listOf(USER_EID.EID) //Specifying custom field to be used as cache key instead of using the fields from the primary key
    backwardsJoin = true
    expression {
      user.userName == entity.userName
    }
  }
}
