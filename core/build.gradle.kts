import net.labymod.labygradle.common.extension.LabyModAnnotationProcessorExtension.ReferenceType

base {
    archivesName.set("paintball-plus-core")
}

dependencies {
    labyProcessor()
    api(project(":api"))
}

labyModAnnotationProcessor {
    referenceType = ReferenceType.DEFAULT
}
