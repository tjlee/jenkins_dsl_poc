import com.setdsl.ServerPatchBuilderMultiJobTemplate

/*
PARAMETERS
 define git from
  define git to
  define version from
   define version to
   define client (default is rus )
   define is to run tests (optional for now)

JOB SEQUENCE
   build makeDistr for git from

   build makeDistr for git to
   build flex for git to

   run patch builder job

   manage artifacts ($COPY_TO)

*/

// todo: define how to hide wrapper jobs

new ServerPatchBuilderMultiJobTemplate(
        name: "build_server_patch",
        description: "Building server patch",
        isToRunTests: false
).build(this)