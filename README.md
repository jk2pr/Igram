

This repository contain Project having implementation of instagram integration.

versionName "1.0"

**#How to use :**
 
  user igram as library in your project
  Add following dependencies in your project
 
        {
 
		 compile project(':igram')
 
        }
 **Steps**
 
 1. Create object of InstagramUtils class using CLIENT_ID,CLIENT_SECRET_ID and CALLBACK.
 2. set UserInfoCallback listner
 
         mApp = new InstagramUtils(this, CLIENT_ID,
                "CLIENT_SECRET_ID", CALL_BACK_URL);
          mApp.setListener(new InstagramUtils.UserInfoCallBack() {
            @Override
            public void onInfoLoad(UserInfo mInfo) {

            }


        });
 
 
 
 3. call authorize method of InstagramUtils class
 
        mApp.authorize();
 
 5. override onActivityResult() in your activity and call handleOnActivityResult
 
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
          super.onActivityResult(requestCode, resultCode, data);
          mApp.handleOnActvityResult(data);
        }
 
 6. getCallback in onInfoLoad() method
 
            public void onInfoLoad(UserInfo mInfo) {

            }
  

     
     
