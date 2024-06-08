cmake_minimum_required(VERSION 3.10)

project(win-app-cxx-password-manager VERSION 1.0.0)

set(ROOT "C:/PasswordManager")
set(RESOURCES "${ROOT}/resources")

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_VERBOSE_MAKEFILE OFF)
set(STATIC_JDK_LINK OFF)

set(OUTPUT_NAME ${PROJECT_NAME}-${CMAKE_BUILD_TYPE}-${PROJECT_VERSION})

set(wxWidgets_LIB_DIR $ENV{WXWIN}/lib/gcc_libx64)
find_package(wxWidgets 3.2 REQUIRED core base xml)

add_definitions(-DJDK="$ENV{JDK}")
add_definitions(-DRESOURCES="${RESOURCES}")

if(STATIC_JDK_LINK MATCHES ON)

    add_definitions(-DSTATIC_JDK_LINK)

    set(OUTPUT_DIRECTORY ${RESOURCES}/jdk-lite/bin/server)

    set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${OUTPUT_DIRECTORY})
    set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${OUTPUT_DIRECTORY})
    set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${OUTPUT_DIRECTORY})
else()
    add_definitions(-DDYNAMIC_JDK_LINK)
endif()

include_directories(${wxWidgets_INCLUDE_DIRS})
include_directories($ENV{JDK}/include)
include_directories($ENV{JDK}/include/win32)
include_directories($ENV{OPENSSL}/include)

add_link_options(-static)
if(CMAKE_BUILD_TYPE MATCHES "Release")
    add_link_options(-s)
endif()

link_directories(${wxWidgets_LIB_DIR})
link_directories($ENV{OPENSSL}/lib/gcc_libx64)

if(STATIC_JDK_LINK MATCHES ON)
    link_directories($ENV{JDK}/lib)
endif()

add_executable(${OUTPUT_NAME}

    src/main.cpp
    src/jnicloudelib.cpp
    #src/cipher.cpp
    #src/xmlfile.cpp
    #src/mainframe.cpp

    #res/iv.o
    #res/salt.o
)

target_link_libraries(${OUTPUT_NAME} ${wxWidgets_LIBRARIES})
target_link_libraries(${OUTPUT_NAME} ssl)
target_link_libraries(${OUTPUT_NAME} crypto)
target_link_libraries(${OUTPUT_NAME} crypt32)
target_link_libraries(${OUTPUT_NAME} ws2_32)

if(STATIC_JDK_LINK MATCHES ON)
    target_link_libraries(${OUTPUT_NAME} jvm)
endif()

add_executable(obj64

    src/main.cpp
    res/test64.o
)

add_executable(obj32

    src/main.cpp
    res/test32.o
)

target_compile_options(obj32 PRIVATE -m32)
target_link_options(obj32 PRIVATE -m32)