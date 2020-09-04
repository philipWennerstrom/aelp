@ECHO ############################################
@ECHO ########## Cleaning all files ... ##########
@ECHO ############################################


@cd AL-Chat
@call ..\Tools\Ant\bin\ant clean

@cd ..

@cd AL-Login
@call ..\Tools\Ant\bin\ant clean

@cd ..

@cd AL-Game
@call ..\Tools\Ant\bin\ant clean

@cd ..

@ECHO ############################################
@ECHO ################# Completed ################
@ECHO ############################################

@PAUSE