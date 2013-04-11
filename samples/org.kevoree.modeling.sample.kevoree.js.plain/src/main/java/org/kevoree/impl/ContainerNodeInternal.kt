package org.kevoree.impl

/**
 * Created by Kevoree Model Generator(KMF).
 * @developers: Gregory Nain, Fouquet Francois
 * Date: 10 avr. 13 Time: 18:33
 * Meta-Model:NS_URI=http://kevoree/1.0
 */
trait ContainerNodeInternal : org.kevoree.container.KMFContainerImpl, org.kevoree.ContainerNode , org.kevoree.impl.NamedElementInternal , org.kevoree.impl.InstanceInternal {
override fun setRecursiveReadOnly(){
if(internal_recursive_readOnlyElem == true){return}
internal_recursive_readOnlyElem = true
val subsubsubsubtypeDefinition = this.getTypeDefinition()
if(subsubsubsubtypeDefinition!= null){ 
subsubsubsubtypeDefinition.setRecursiveReadOnly()
}

val subsubsubsubdictionary = this.getDictionary()
if(subsubsubsubdictionary!= null){ 
subsubsubsubdictionary.setRecursiveReadOnly()
}

for(sub in this.getComponents()){
sub.setRecursiveReadOnly()
}

for(sub in this.getHosts()){
sub.setRecursiveReadOnly()
}

setInternalReadOnly()
}
internal var _components_java_cache : List<org.kevoree.ComponentInstance>?
internal val _components : java.util.HashMap<Any,org.kevoree.ComponentInstance>
internal var _hosts_java_cache : List<org.kevoree.ContainerNode>?
internal val _hosts : java.util.HashMap<Any,org.kevoree.ContainerNode>
override fun delete(){
_dictionary?.delete()
for(el in _components){
el.value.delete()
}
_components?.clear()
_components_java_cache = null
_hosts?.clear()
_hosts_java_cache = null
}

override fun getComponents() : List<org.kevoree.ComponentInstance> {
return if(_components_java_cache != null){
_components_java_cache as List<org.kevoree.ComponentInstance>
} else {
val tempL = java.util.ArrayList<org.kevoree.ComponentInstance>()
tempL.addAll(_components.values().toList())
_components_java_cache = tempL
tempL
}
}

override fun setComponents(components : List<org.kevoree.ComponentInstance> ) {
if(isReadOnly()){throw Exception("This model is ReadOnly. Elements are not modifiable.")}
if(components == null){ throw IllegalArgumentException("The list in parameter of the setter cannot be null. Use removeAll to empty a collection.") }
_components_java_cache=null
if(_components!= components){
_components.clear()
for(el in components){
_components.put(el.getName(),el)
}
for(elem in components){
(elem as org.kevoree.container.KMFContainerImpl).setEContainer(this,{()->this.removeComponents(elem)})
(elem as org.kevoree.container.KMFContainerImpl).setContainmentRefName("components")
}
}

}

override fun addComponents(components : org.kevoree.ComponentInstance) {
if(isReadOnly()){throw Exception("This model is ReadOnly. Elements are not modifiable.")}
_components_java_cache=null
(components as org.kevoree.container.KMFContainerImpl).setEContainer(this,{()->this.removeComponents(components)})
(components as org.kevoree.container.KMFContainerImpl).setContainmentRefName("components")
_components.put(components.getName(),components)
}

override fun addAllComponents(components :List<org.kevoree.ComponentInstance>) {
if(isReadOnly()){throw Exception("This model is ReadOnly. Elements are not modifiable.")}
_components_java_cache=null
for(el in components){
_components.put(el.getName(),el)
}
for(el in components){
(el as org.kevoree.container.KMFContainerImpl).setEContainer(this,{()->this.removeComponents(el)})
(el as org.kevoree.container.KMFContainerImpl).setContainmentRefName("components")
}
}


override fun removeComponents(components : org.kevoree.ComponentInstance) {
if(isReadOnly()){throw Exception("This model is ReadOnly. Elements are not modifiable.")}
_components_java_cache=null
if(_components.size() != 0 && _components.containsKey(components.getName())) {
_components.remove(components.getName())
(components!! as org.kevoree.container.KMFContainerImpl).setEContainer(null,null)
(components!! as org.kevoree.container.KMFContainerImpl).setContainmentRefName(null)
}
}

override fun removeAllComponents() {
if(isReadOnly()){throw Exception("This model is ReadOnly. Elements are not modifiable.")}
for(elm in getComponents()!!){
val el = elm
(el as org.kevoree.container.KMFContainerImpl).setEContainer(null,null)
(el as org.kevoree.container.KMFContainerImpl).setContainmentRefName(null)
}
_components_java_cache=null
_components.clear()
}

override fun getHosts() : List<org.kevoree.ContainerNode> {
return if(_hosts_java_cache != null){
_hosts_java_cache as List<org.kevoree.ContainerNode>
} else {
val tempL = java.util.ArrayList<org.kevoree.ContainerNode>()
tempL.addAll(_hosts.values().toList())
_hosts_java_cache = tempL
tempL
}
}

override fun setHosts(hosts : List<org.kevoree.ContainerNode> ) {
if(isReadOnly()){throw Exception("This model is ReadOnly. Elements are not modifiable.")}
if(hosts == null){ throw IllegalArgumentException("The list in parameter of the setter cannot be null. Use removeAll to empty a collection.") }
_hosts_java_cache=null
if(_hosts!= hosts){
_hosts.clear()
for(el in hosts){
_hosts.put(el.getName(),el)
}
}

}

override fun addHosts(hosts : org.kevoree.ContainerNode) {
if(isReadOnly()){throw Exception("This model is ReadOnly. Elements are not modifiable.")}
_hosts_java_cache=null
_hosts.put(hosts.getName(),hosts)
}

override fun addAllHosts(hosts :List<org.kevoree.ContainerNode>) {
if(isReadOnly()){throw Exception("This model is ReadOnly. Elements are not modifiable.")}
_hosts_java_cache=null
for(el in hosts){
_hosts.put(el.getName(),el)
}
}


override fun removeHosts(hosts : org.kevoree.ContainerNode) {
if(isReadOnly()){throw Exception("This model is ReadOnly. Elements are not modifiable.")}
_hosts_java_cache=null
if(_hosts.size() != 0 && _hosts.containsKey(hosts.getName())) {
_hosts.remove(hosts.getName())
}
}

override fun removeAllHosts() {
if(isReadOnly()){throw Exception("This model is ReadOnly. Elements are not modifiable.")}
_hosts_java_cache=null
_hosts.clear()
}
override fun getClonelazy(subResult : java.util.HashMap<Any,Any>, _factories : org.kevoree.factory.MainFactory, mutableOnly: Boolean) {
if(mutableOnly && isRecursiveReadOnly()){return}
		val selfObjectClone = _factories.getKevoreeFactory().createContainerNode()
		selfObjectClone.setName(this.getName())
		selfObjectClone.setMetaData(this.getMetaData())
		subResult.put(this,selfObjectClone)
val subsubsubsubdictionary = this.getDictionary()
if(subsubsubsubdictionary!= null){ 
(subsubsubsubdictionary as org.kevoree.impl.DictionaryInternal ).getClonelazy(subResult, _factories,mutableOnly)
}

for(sub in this.getComponents()){
(sub as org.kevoree.impl.ComponentInstanceInternal).getClonelazy(subResult, _factories,mutableOnly)
}

	}
override fun resolve(addrs : java.util.HashMap<Any,Any>,readOnly:Boolean, mutableOnly: Boolean) : Any {
if(mutableOnly && isRecursiveReadOnly()){
return this
}
val clonedSelfObject = addrs.get(this) as org.kevoree.impl.ContainerNodeInternal
if(this.getTypeDefinition()!=null){
if(mutableOnly && this.getTypeDefinition()!!.isRecursiveReadOnly()){
clonedSelfObject.setTypeDefinition(this.getTypeDefinition()!!)
} else {
clonedSelfObject.setTypeDefinition(addrs.get(this.getTypeDefinition()) as org.kevoree.TypeDefinition)
}
}

if(this.getDictionary()!=null){
if(mutableOnly && this.getDictionary()!!.isRecursiveReadOnly()){
clonedSelfObject.setDictionary(this.getDictionary()!!)
} else {
clonedSelfObject.setDictionary(addrs.get(this.getDictionary()) as org.kevoree.Dictionary)
}
}

for(sub in this.getComponents()){
if(mutableOnly && sub.isRecursiveReadOnly()){
clonedSelfObject.addComponents(sub)
} else {
clonedSelfObject.addComponents(addrs.get(sub) as org.kevoree.ComponentInstance)
}
		}

for(sub in this.getHosts()){
if(mutableOnly && sub.isRecursiveReadOnly()){
clonedSelfObject.addHosts(sub)
} else {
clonedSelfObject.addHosts(addrs.get(sub) as org.kevoree.ContainerNode)
}
		}

val subsubsubdictionary = this.getDictionary()
if(subsubsubdictionary!=null){ 
(subsubsubdictionary as org.kevoree.impl.DictionaryInternal).resolve(addrs,readOnly,mutableOnly)
}

for(sub in this.getComponents()){
			(sub as org.kevoree.impl.ComponentInstanceInternal ).resolve(addrs,readOnly,mutableOnly)
		}

		if(readOnly){clonedSelfObject.setInternalReadOnly()}
return clonedSelfObject
}
override fun internalGetKey() : String {
return getName()}
override fun path() : String? {
val container = eContainer()
if(container != null) {
val parentPath = container.path()
return  if(parentPath == null){""}else{parentPath + "/"} + internal_containmentRefName + "["+internalGetKey()+"]"
} else {
return null
}
}
override fun findComponentsByID(key : String) : org.kevoree.ComponentInstance? {
return _components.get(key)
}
override fun findHostsByID(key : String) : org.kevoree.ContainerNode? {
return _hosts.get(key)
}
override fun findByPath(query : String) : Any? {
val firstSepIndex = query.indexOf('[')
var queryID = ""
var extraReadChar = 2
val relationName = query.substring(0,query.indexOf('['))
if(query.indexOf('{') == firstSepIndex +1){
queryID = query.substring(query.indexOf('{')+1,query.indexOf('}'))
extraReadChar = extraReadChar + 2
} else {
queryID = query.substring(query.indexOf('[')+1,query.indexOf(']'))
}
var subquery = query.substring(relationName.size+queryID.size+extraReadChar,query.size)
if (subquery.indexOf('/') != -1){
subquery = subquery.substring(subquery.indexOf('/')+1,subquery.size)
}
return when(relationName) {
"typeDefinition" -> {
getTypeDefinition()
}
"components" -> {
val objFound = findComponentsByID(queryID)
if(subquery != "" && objFound != null){
throw Exception("KMFQL : rejected sucessor")
} else {objFound}
}
"hosts" -> {
val objFound = findHostsByID(queryID)
if(subquery != "" && objFound != null){
objFound.findByPath(subquery)
} else {objFound}
}
else -> {}
}
}
}
