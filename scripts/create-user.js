db.createUser({ user: "admin",
  pwd: "123456",
  customData: {},  
  roles: [
    { role: "dbAdmin", db: "weatherbot" }
  , "readWrite"]
},{})