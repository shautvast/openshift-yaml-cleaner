Cleaner for yaml files that openshift has created using oc get [resource] -o yaml. 

NB. Work in progress: it works fine, but it might need adding more remove actions for certain outputs.

usage: nl.sander.Cleaner \<file>

YamlObject exposes an xpath/yamlpath like api for lookup and remove, but it's as simple as my current needs. See Cleaner.java for use cases. 