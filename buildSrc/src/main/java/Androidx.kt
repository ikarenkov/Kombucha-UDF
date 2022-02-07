object androidx {

    val annotation = "androidx.annotation:annotation:${versions.androidx.annotation}".dep

    val cardview = deps(
        "androidx.cardview:cardview:${versions.androidx.cardview}".dep,
        annotation
    )
    private val collection = deps(
        annotation,
        "androidx.collection:collection:${versions.androidx.collection}".dep
    )
    private val versionedparcelable = deps(
        "androidx.versionedparcelable:versionedparcelable:${versions.androidx.versionedparcelable}".dep,
        annotation,
        collection
    )
    private val core_common = deps(
        annotation,
        "androidx.arch.core:core-common:${versions.androidx.core_common}".dep
    )
    private val lifecycle_common = deps(
        annotation,
        "androidx.lifecycle:lifecycle-common:${versions.androidx.lifecycle}".dep,
        "androidx.lifecycle:lifecycle-common-java8:${versions.androidx.lifecycle}".dep
    )

    private val lifecycle_runtime = deps(
        "androidx.lifecycle:lifecycle-runtime:${versions.androidx.lifecycle}".dep,
        core_common,
        lifecycle_common
    )

    private val lifecycle_viewmodel = deps(
        annotation,
        "androidx.lifecycle:lifecycle-viewmodel:${versions.androidx.lifecycle}".dep
    )

    private val lifecycle_viewmodel_ktx = deps(
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${versions.androidx.lifecycle}".dep,
        lifecycle_viewmodel,
        kotlinx.coroutines_android
    )

    val viewmodel = deps(
        lifecycle_viewmodel_ktx
    )

    private val savedstate = deps(
        "androidx.savedstate:savedstate:${versions.androidx.savedstate}".dep,
        "androidx.savedstate:savedstate-ktx:${versions.androidx.savedstate}".dep,
        annotation,
        core_common,
        lifecycle_common
    )

    private val core_runtime = deps(
        "androidx.arch.core:core-runtime:${versions.androidx.arch}".dep,
        annotation,
        core_common
    )

    private val lifecycle_livedate_core = deps(
        "androidx.lifecycle:lifecycle-livedata-core:${versions.androidx.lifecycle}".dep,
        core_common,
        core_runtime
    )

    private val lifecycle_viewmodel_savedstate = deps(
        "androidx.lifecycle:lifecycle-viewmodel-savedstate:${versions.androidx.lifecycle}".dep,
        annotation,
        lifecycle_livedate_core,
        lifecycle_viewmodel,
        savedstate
    )

    val core = deps(
        "androidx.core:core:${versions.androidx.core}".dep,
        annotation,
        lifecycle_runtime,
        versionedparcelable
    )

    val core_ktx = deps(
        "androidx.core:core-ktx:${versions.androidx.core}".dep,
        annotation,
        core
    )

    private val activity = deps(
        "androidx.activity:activity:${versions.androidx.activity}".dep,
        annotation,
        core,
        lifecycle_runtime,
        lifecycle_viewmodel,
        lifecycle_viewmodel_savedstate,
        savedstate
    )

    private val loader = deps(
        "androidx.loader:loader:${versions.androidx.loader}".dep,
        annotation,
        collection,
        core,
        lifecycle_livedate_core,
        lifecycle_viewmodel
    )

    private val customview = deps(
        "androidx.customview:customview:${versions.androidx.customview}".dep,
        annotation,
        core
    )

    private val viewpager = deps(
        "androidx.viewpager:viewpager:${versions.androidx.viewpager}".dep,
        annotation,
        core,
        customview
    )

    val fragment = deps(
        "androidx.fragment:fragment:${versions.androidx.fragment}".dep,
        annotation,
        activity,
        collection,
        core,
        lifecycle_livedate_core,
        lifecycle_viewmodel,
        lifecycle_viewmodel_savedstate,
        loader,
        viewpager
    )

    private val drawerlayout = deps(
        "androidx.drawerlayout:drawerlayout:${versions.androidx.drawerlayout}".dep,
        annotation,
        core,
        customview
    )

    private val documentfile = deps(
        "androidx.documentfile:documentfile:${versions.androidx.documentfile}".dep,
        annotation
    )
    private val localbroadcastmanager = deps(
        "androidx.localbroadcastmanager:localbroadcastmanager:${versions.androidx.localbroadcastmanager}".dep,
        annotation
    )
    private val print = deps(
        "androidx.print:print:1.0.0".dep,
        annotation
    )
    val legacy_utils = deps(
        "androidx.legacy:legacy-support-core-utils:${versions.androidx.legacy}".dep,
        documentfile,
        localbroadcastmanager,
        print,
        annotation,
        core,
        loader
    )

    val vectordrawable = deps(
        "androidx.vectordrawable:vectordrawable:${versions.androidx.vectordrawable}".dep,
        annotation,
        collection,
        core
    )

    private val asynclayoutinflater = deps(
        "androidx.asynclayoutinflater:asynclayoutinflater:${versions.androidx.asynclayoutinflater}".dep,
        annotation,
        core
    )

    private val coordinatorlayout = deps(
        "androidx.coordinatorlayout:coordinatorlayout:${versions.androidx.coordinatorlayout}".dep,
        annotation,
        collection,
        core,
        customview
    )

    private val interpolator = deps(
        "androidx.interpolator:interpolator:${versions.androidx.interpolator}".dep,
        annotation
    )
    private val cursoradapter = deps(
        "androidx.cursoradapter:cursoradapter:${versions.androidx.cursoradapter}".dep,
        annotation
    )

    private val slidingpanelayout = deps(
        "androidx.slidingpanelayout:slidingpanelayout:${versions.androidx.slidingpanelayout}".dep,
        annotation,
        customview
    )

    val swiperefreshlayout = deps(
        "androidx.swiperefreshlayout:swiperefreshlayout:${versions.androidx.swiperefreshlayout}".dep,
        annotation,
        core,
        interpolator
    )

    val paging = transitiveDeps(
        "androidx.paging:paging-runtime:${versions.androidx.paging}",
        "androidx.paging:paging-common:${versions.androidx.paging}"
    )

    val legacy_ui = deps(
        "androidx.legacy:legacy-support-core-ui:${versions.androidx.legacy}".dep,
        asynclayoutinflater,
        coordinatorlayout,
        cursoradapter,
        interpolator,
        slidingpanelayout,
        swiperefreshlayout,
        core,
        customview,
        drawerlayout,
        legacy_utils,
        viewpager
    )
    val recyclerview = deps(
        "androidx.recyclerview:recyclerview:${versions.androidx.recyclerview}".dep,
        annotation,
        collection,
        core,
        customview
    )
    val transition = deps(
        "androidx.transition:transition:${versions.androidx.transition}".dep,
        annotation,
        collection,
        core,
        lifecycle_runtime
    )
    private val navigation_common = deps(
        "androidx.navigation:navigation-common:${versions.androidx.navigation}".dep,
        annotation,
        collection,
        core
    )

    val appcompat = deps(
        "androidx.appcompat:appcompat:${versions.androidx.appcompat}".dep,
        "androidx.appcompat:appcompat-resources:${versions.androidx.appcompat}".dep,
        annotation,
        core,
        cursoradapter,
        drawerlayout,
        fragment,
        collection
    )
    val constraintlayout = deps(
        "androidx.constraintlayout:constraintlayout:${versions.androidx.constraintlayout}".dep,
        "androidx.constraintlayout:constraintlayout-solver:${versions.androidx.constraintlayout}".dep,
        appcompat,
        core
    )

    private val navigation_ui_ktx = deps(
        "androidx.navigation:navigation-ui-ktx:${versions.androidx.navigation}",
        "androidx.navigation:navigation-ui:${versions.androidx.navigation}"
    )

    private val navigation_common_ktx = deps(
        "androidx.navigation:navigation-common-ktx:${versions.androidx.navigation}".dep,
        annotation,
        collection,
        core
    )

    private val navigation_runtime = deps(
        "androidx.navigation:navigation-runtime:${versions.androidx.navigation}".dep,
        activity,
        lifecycle_viewmodel,
        lifecycle_viewmodel_savedstate,
        navigation_common,
        savedstate
    )

    private val navigation_fragment = deps(
        "androidx.navigation:navigation-fragment:${versions.androidx.navigation}".dep,
        fragment,
        navigation_runtime
    )
    private val navigation_fragment_ktx = deps(
        "androidx.navigation:navigation-fragment-ktx:${versions.androidx.navigation}".dep,
        navigation_fragment,
        navigation_runtime,
        fragment
    )

    val navigation = deps(
        navigation_fragment_ktx,
        navigation_ui_ktx,
        navigation_common_ktx
    )

    private val sqlite = deps(
        "androidx.sqlite:sqlite:${versions.androidx.sqlite}".dep,
        "androidx.sqlite:sqlite-framework:${versions.androidx.sqlite}".dep
    )

    val room = deps(
        "androidx.room:room-runtime:${versions.androidx.room}".dep,
        "androidx.room:room-ktx:${versions.androidx.room}".dep,
        "androidx.room:room-common:${versions.androidx.room}".dep,
        "androidx.room:room-compiler:${versions.androidx.room}".kapt,
        sqlite
    )

    object compose {

        val runtime = deps(
            deps(
                "androidx.compose.runtime:runtime:${versions.androidx.compose}",
                "androidx.compose.runtime:runtime-saveable:${versions.androidx.compose}",
            ),
            kotlinx.coroutines_android
        )

        private val ui = deps(
            "androidx.compose.ui:ui:${versions.androidx.compose}",
            "androidx.compose.ui:ui-geometry:${versions.androidx.compose}",
            "androidx.compose.ui:ui-graphics:${versions.androidx.compose}",
            "androidx.compose.ui:ui-unit:${versions.androidx.compose}",
            "androidx.compose.ui:ui-text:${versions.androidx.compose}"
        )

        // Tooling support (Previews, etc.)
        private val uiTooling = deps(
            "androidx.compose.ui:ui-tooling:${versions.androidx.compose}",
            "androidx.compose.ui:ui-tooling-preview:${versions.androidx.compose}",
            "androidx.compose.ui:ui-tooling-data:${versions.androidx.compose}",
        )

        private val animation = deps(
            "androidx.compose.animation:animation:${versions.androidx.compose}",
            "androidx.compose.animation:animation-core:${versions.androidx.compose}",
        )

        // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
        private val foundation = deps(
            "androidx.compose.foundation:foundation:${versions.androidx.compose}",
            "androidx.compose.foundation:foundation-layout:${versions.androidx.compose}"
        )

        // Integration with activities
        val activity = "androidx.activity:activity-compose:${versions.androidx.activity}".dep

        private val material = deps(
            "androidx.compose.material:material:${versions.androidx.compose}",
            "androidx.compose.material:material-icons-core:${versions.androidx.compose}",
            "androidx.compose.material:material-ripple:${versions.androidx.compose}"
        )

        private val preview = deps(
            deps(
                "androidx.savedstate:savedstate:${versions.androidx.savedstate}",
                "androidx.lifecycle:lifecycle-runtime:${versions.androidx.lifecycle}",
                "androidx.lifecycle:lifecycle-common:${versions.androidx.lifecycle}",
                "androidx.arch.core:core-common:${versions.androidx.core_common}",
                "androidx.lifecycle:lifecycle-viewmodel:${versions.androidx.lifecycle}",
                "androidx.activity:activity:${versions.androidx.activity}",
                "androidx.core:core:${versions.androidx.core}",
                "androidx.collection:collection:${versions.androidx.collection}",
            ),
            activity
        )

        val baseNoMaterial = deps(
            foundation,
            ui,
            uiTooling,
            annotation,
            animation,
            runtime,
            preview
        )

        val base = deps(
            baseNoMaterial,
            material
        )

        val constraintLayout = "androidx.constraintlayout:constraintlayout-compose:1.0.0-rc02".dep
    }
}