/**
 * 🏥 Project: The Emergency Response System
 * Imagine you are building the backend for a City Hospital System.
 *
 * Phase 1: The Setup (Data Structure)
 * Interface: Create Treatable. It should have a property urgencyLevel: Int (1 to 10) and a property isStabilized: Boolean.
 */
interface Treatable{
    val urgencyLevel: Int
    val isStabilized: Boolean
}

/**
 * Abstract Class: Patient. Properties: id: Int, name: String, and age: Int.
 *
 **/
abstract class Patient(
    open val id : Int,
    open val name : String,
    open val age : Int,
){

}

/*** Subclasses:
 *
 * TraumaPatient: Inherits Patient and Treatable. Add property requiresSurgery: Boolean.
 *
 * StandardPatient: Inherits Patient. Add property roomNumber: String.
 *
 * **/
data class TraumaPatient(
    override val id: Int,
    override val name: String,
    override val age: Int,
    override val urgencyLevel: Int,
    override val isStabilized: Boolean,
    val requiresSurgery: Boolean)
    : Patient(id,name,age),Treatable {


}

data class StandardPatient(
    override val id: Int,
    override val name: String,
    override val age: Int,
    val roomNumber: String
) :Patient(id,name,age) {

}


/**
 * Phase 2: The Logic (HospitalManager)
 * Create a HospitalManager class with a MutableList<Patient> and write these functions using the tools we practiced:
 */
class HospitalManager() {

    val patientList = mutableListOf<Patient>()

//The Guarded Add (require): Create addPatient(p: Patient). Use require to ensure the id is positive and the name is not blank.

    fun addPatient(p: Patient) {
        require(p.id > 0 && !p.name.isEmpty()) { " id must be > 0 and name cannot be blank" }
        patientList.add(p)
    }


//The Priority Queue (sortedWith): Return a list of only Treatable patients,
// sorted by highest urgencyLevel first. If urgency is the same, sort by youngest age first.

    fun treatablePatients(): List<Treatable> {
        return patientList.filterIsInstance<Treatable>()
            .sortedWith(
                compareByDescending<Treatable> { it.urgencyLevel }
                    .thenBy { (it as Patient).age } // Cast to Patient to see the age
            )
    }

}

/**
 *
 * The Priority Queue (sortedWith): Return a list of only Treatable patients, sorted by highest urgencyLevel first. If urgency is the same, sort by youngest age first.
 *
 * The Triage Split (partition): Split all Treatable patients into two lists: those who are isStabilized and those who are not. Return a string: "Critical: X, Ready for Ward: Y".
 *
 * The Surgery Finder (filterIsInstance): Find all TraumaPatients who requiresSurgery and are NOT yet isStabilized.
 *
 * The Safe Search (runCatching): Create findPatientById(id: Int). If the patient isn't found, throw a NoSuchElementException. Wrap the call in runCatching so it returns a "Success" or "Failure" message instead of crashing.
 *
 *
 */