import groovy.json.JsonSlurper
import groovy.json.JsonBuilder
import java.nio.file.Files

def bucketList = [
    new Bucket(
        path: "Main",
        name: "main",
        usage: "scoop bucket add main"
    ),
    new Bucket(
        path: "scoop-extras",
        name: "extras",
        usage: "scoop bucket add extras"
    ),
    new Bucket(
        path: "Versions",
        name: "versions",
        usage: "scoop bucket add versions"
    )
]

def buckets = [:]

def slurper = new JsonSlurper()

for (bucket in bucketList) {
    buckets[bucket.name] = bucket
    for (file in new File(bucket.path, "bucket/").listFiles()) {
        def fileName = file.getName()
        if (fileName.endsWith(".json")) {
            def pkg = fileName.substring(0, fileName.length() - 5)
            def content = new String(Files.readAllBytes(file.toPath()))
            def jsonData = slurper.parseText(content)

            buckets[bucket.name].packages.add(new ScoopPackage(
                pkg: pkg,
                description: jsonData.description,
                version: jsonData.version,
                website: jsonData.homepage
            ))
        }
    }
}

def targetFile = new File('../pkglist.json')
def bucketJson = new JsonBuilder(buckets).toPrettyString()

Files.write(targetFile.toPath(), bucketJson.getBytes())

class Bucket {
    String path
    String name
    String usage

    def packages = []
}

class ScoopPackage {

    String pkg
    String description
    String version
    String website
}