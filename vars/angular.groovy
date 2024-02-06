def call(){
    node{
        common.lintChecks()
        env.ARGS="-Dsonar.sources=."
        common.sonarChecks()
        common.testCases()
        env.NEXUS_URL="172.31.25.180"
        common.artifacts()
    }
}