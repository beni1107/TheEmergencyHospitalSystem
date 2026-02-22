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
            .sortedWith(compareByDescending<Treatable> { it.urgencyLevel }
                .thenBy { (it as Patient).age } // Cast to Patient to see the age
            )
    }

    fun treatablePatients1(): List<Treatable> {
        return patientList.filterIsInstance<Treatable>()
            .sortedWith(compareByDescending<Treatable> { it.urgencyLevel }.thenBy {
                (it as Patient).age
            })

    }


    //The Triage Split (partition): Split all Treatable patients into two lists:
// those who are isStabilized and those who are not. Return a string: "Critical: X, Ready for Ward: Y".
    fun patientsSplit() {
        val (ward, critical) = patientList.filterIsInstance<Treatable>()
            .partition { patient -> patient.isStabilized }
        println("Critical : ${critical.size}, Ready for Ward : ${ward.size}")
    }


    //The Surgery Finder (filterIsInstance): Find all TraumaPatients who requiresSurgery and are NOT yet isStabilized.
    fun surgeryFinder(): List<TraumaPatient> {
        return patientList.filterIsInstance<TraumaPatient>()
            .filter { patient -> patient.requiresSurgery && !patient.isStabilized }
    }
    /**
     * The Safe Search (runCatching): Create findPatientById(id: Int).
     * If the patient isn't found, throw a NoSuchElementException.
     * Wrap the call in runCatching so it returns a "Success" or "Failure" message instead of crashing.
     */
    fun findPatientyById(id: Int): String {
         val result = runCatching {
             patientList.first { patient -> patient.id == id }
         }

      return  result.fold(
            onSuccess = {it.name},
            onFailure = {"Failure not found"}

        )

    }


    /**
     * The "Strict Admission" (require)
     * Create a function admitToRoom(patient: Patient, roomNumber: String).
     * Use require to ensure the roomNumber starts with the letter "R" (e.g., "R101").
     * Use require that if the patient is a TraumaPatient, they cannot be admitted to a room unless they are already isStabilized.
     * */
    fun admitToRoom1(patient: Patient, roomNumber: String): String {
        return runCatching {
            require(roomNumber.startsWith('R')) { "Room number must start with R" }

            if(patient is TraumaPatient) {
                require(patient.isStabilized) {"cannot admit : Trauma patient is not stabilized"}
            }
            "Patient admitted to the $roomNumber"
        }.getOrElse {
            error -> error.message ?: "Unknow admisison error"
        }
    }
    fun admitToRoom(patient: Patient, roomNumber: String): String {
        return runCatching {
            // 1. Check room format
            require(roomNumber.startsWith('R')) { "Room number must start with R" }

            // 2. Check stability for Trauma
            if (patient is TraumaPatient) {
                require(patient.isStabilized) { "Cannot admit: Trauma Patient is not stabilized!" }
            }

            "Patient admitted to $roomNumber" // This is the success result
        }.getOrElse { error ->
            error.message ?: "Unknown admission error"
        }
    }
    /***
     * Calculate the total urgencyLevel of all Treatable patients using sumOf.
     * Count how many TraumaPatients currently require surgery.
     * Return: "Total Urgency Score: X | Surgeries Pending: Y".
     * */
    fun getHospitalLoad(): String {
       val totalUrgencyScore = patientList.filterIsInstance<Treatable>().sumOf { patient -> patient.urgencyLevel }

       val surgeryCount = patientList.filterIsInstance<TraumaPatient>().count{it.requiresSurgery}

       return "Total urgency score: $totalUrgencyScore | Surgeries Pending : $surgeryCount"
    }

 /**
  * Create a function getHighestUrgencyName(): String.
  * Use runCatching to find the single patient with the highest urgencyLevel.
  * Hint: Use maxByOrNull { it.urgencyLevel }.
  * If the list is empty, maxByOrNull returns null. Force an error or handle the null so that getOrElse returns "No patients in triage".
  *
  *
  * **/


 fun getHighestUrgencyName(): String {
     return runCatching {
         patientList.filterIsInstance<TraumaPatient>()
             .maxByOrNull { it.urgencyLevel }?.name
     }.getOrElse { error -> error.message }
 }
}