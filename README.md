# Resorcerer
A library to download multiple images/files at runtime.

# Usage

Add links using addLink(String) or addMultipleLinks(ArrayList()) and call build() or buildWithListener() to start download of files asynchronusly.

```
ReSorcerer.Builder()
            .setContext(this)
            .addLink("http://icons.iconarchive.com/icons/thesquid.ink/free-flat-sample/1024/bell-icon.png")
            .addLink("http://icons.iconarchive.com/icons/thesquid.ink/free-flat-sample/1024/cap-icon.png")
            .addLink("http://icons.iconarchive.com/icons/thesquid.ink/free-flat-sample/1024/football-icon.png")
            .addLink("https://gradeup.co/liveData/f/2019/5/weekly-oneliner-8th-to-14th-may-eng-50.pdf")
            .setStorage(ReSorcerer.Storage.INTERNAL)
            .buildWithListener(object : ReSorcerer.OnResultInterface {
                override fun onComplete(failedList: ArrayList<String>) {
                    image.setImageRes("http://icons.iconarchive.com/icons/thesquid.ink/free-flat-sample/1024/cap-icon.png")
                }
            })
```

# Installation

Add it in your root build.gradle at the end of repositories:

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Add the dependency
```
dependencies {
	        implementation 'com.github.gunjitdhawan:Resorcerer:1.0'
	}
```
