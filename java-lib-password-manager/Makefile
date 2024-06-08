ROOT = C:/PasswordManager

SRC_APP = \
	${ROOT}/java-lib-password-manager/src/CloudApp.java

SRC_LIB = \
	${ROOT}/java-lib-password-manager/src/CloudLib.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/ContentRangeResponse.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/Credentials.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/DownloadListener.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/CancelledDownloadException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/CancelledPropfindException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/CancelledUploadingException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/DownloadNoSpaceAvailableException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/DuplicateFolderException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/FileModifiedException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/FileNotModifiedException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/FilesLimitExceededServerException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/FileTooBigServerException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/InsufficientStorageException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/IntermediateFolderNotExistException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/PreconditionFailedException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/RangeNotSatisfiableException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/RemoteFileNotFoundException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/RequestInterruptedException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/ServerException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/ServerWebdavException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/ServiceUnavailableWebdavException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/SharingException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/UnknownServerWebdavException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/UnsupportedMediaTypeException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/WebdavClientInitException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/WebdavException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/WebdavFileNotFoundException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/WebdavForbiddenException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/WebdavInvalidUserException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/WebdavIOException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/WebdavNotAuthorizedException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/WebdavSharingForbiddenException.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/exceptions/WebdavUserNotInitialized.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/FileProgressHttpEntity.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/ListItem.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/ListParser.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/ListParsingHandler.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/Parser.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/ProgressListener.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/SSLSocketFactoryWithTimeout.java \
	${ROOT}/java-lib-password-manager/src/com/yandex/disk/client/TransportClient.java

OBJ_LIB = .

OBJ_APP = .

CLASS_PATH_LIB = \
	${ROOT}/resource/library/commons-logging-1.2.jar;\
	${ROOT}/resource/library/httpclient-4.5.13.jar;\
	${ROOT}/resource/library/httpcore-4.4.16.jar;\
	${ROOT}/resource/library/xmlpull-1.1.3.4a.jar

CLASS_PATH_APP = \
	${ROOT}/resource/library/cloud-lib.jar;\
	${ROOT}/resource/library/commons-logging-1.2.jar;\
	${ROOT}/resource/library/httpclient-4.5.13.jar;\
	${ROOT}/resource/library/httpcore-4.4.16.jar;\
	${ROOT}/resource/library/xmlpull-1.1.3.4a.jar

all: lib app

lib:
	javac -cp ${CLASS_PATH_LIB} -d build-lib ${SRC_LIB}
	jar cf ${ROOT}/resource/library/cloud-lib.jar -C build-lib ${OBJ_LIB}

app:
	javac -cp ${CLASS_PATH_APP} -d build-app ${SRC_APP}

	cd build-app && jar -xf ${ROOT}/resource/library/cloud-lib.jar
	cd build-app && jar -xf ${ROOT}/resource/library/commons-logging-1.2.jar
	cd build-app && jar -xf ${ROOT}/resource/library/httpclient-4.5.13.jar
	cd build-app && jar -xf ${ROOT}/resource/library/httpcore-4.4.16.jar
	cd build-app && jar -xf ${ROOT}/resource/library/xmlpull-1.1.3.4a.jar

	jar cfm ${ROOT}/resource/library/cloud-app.jar src/manifest.mf -C build-app ${OBJ_APP}

# -cp <folder>/<file>;<folder>/<file>;<folder>/<file>; ...        - include extern library
# -cp <folder>/*
# -d  <folder out> <folder>/<file> <folder>/<file>                - output folder
# c                                                               - create new archive
# f                                                               - name archive file
# m                                                               - add manifest
# -C <folder> <file>  <folder> .                                  - add obj or jar