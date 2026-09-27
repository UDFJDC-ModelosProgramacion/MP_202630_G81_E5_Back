package co.edu.udistrital.mdp.pets.exceptions;

public final class ErrorMessage {

	// Pet
	public static final String PET_NOT_FOUND = "The pet with the given id was not found";
	public static final String PET_NOT_VALID = "Pet data is not valid";
	public static final String PET_AGE_INVALID = "Pet age must be zero or a positive number";
	public static final String PET_ASSOCIATED_ADOPTIONS = "Unable to delete the pet because it has associated adoptions";
	public static final String PET_ASSOCIATED_PHOTOS = "Unable to delete the pet because it has associated photos";
	public static final String PET_NOT_AVAILABLE = "The pet is not available for adoption";

	// Photo
	public static final String PHOTO_NOT_FOUND = "The photo with the given id was not found";
	public static final String PHOTO_URL_NOT_VALID = "Photo url is not valid";
	public static final String PHOTO_PARENT_NOT_VALID = "The photo must be associated to a pet or to a shelter";
	public static final String PHOTO_NOT_ASSOCIATED_TO_PET = "The photo is not associated to the pet";
	public static final String PHOTO_NOT_ASSOCIATED_TO_SHELTER = "The photo is not associated to the shelter";

	// Video
	public static final String VIDEO_NOT_FOUND = "The video with the given id was not found";
	public static final String VIDEO_URL_NOT_VALID = "Video url is not valid";
	public static final String VIDEO_NOT_ASSOCIATED_TO_SHELTER = "The video is not associated to the shelter";

	// Shelter
	public static final String SHELTER_NOT_FOUND = "The shelter with the given id was not found";
	public static final String SHELTER_NOT_VALID = "Shelter is not valid";
	public static final String SHELTER_ASSOCIATED_PETS = "Unable to delete the shelter because it has associated pets";
	public static final String SHELTER_ASSOCIATED_VETERINARIANS = "Unable to delete the shelter because it has associated veterinarians";
	public static final String SHELTER_ASSOCIATED_NOTIFICATIONS = "Unable to delete the shelter because it has associated notifications";
	public static final String SHELTER_ASSOCIATED_PHOTOS = "Unable to delete the shelter because it has associated photos";
	public static final String SHELTER_ASSOCIATED_VIDEOS = "Unable to delete the shelter because it has associated videos";
	public static final String SHELTER_ASSOCIATED_SHELTER_EVENTS = "Unable to delete the shelter because it has associated shelter events";

	// ShelterEvent
	public static final String SHELTER_EVENT_NOT_FOUND = "The shelter event with the given id was not found";
	public static final String SHELTER_EVENT_NOT_VALID = "Shelter event data is not valid";
	public static final String SHELTER_EVENT_NOT_ASSOCIATED_TO_SHELTER = "The shelter event is not associated to the shelter";

	// Message
	public static final String MESSAGE_NOT_FOUND = "The message with the given id was not found";
	public static final String MESSAGE_NOT_VALID = "Message data is not valid";
	public static final String MESSAGE_DATE_INVALID = "Message sent date cannot be in the future";
	public static final String MESSAGE_NOT_ASSOCIATED_TO_ADOPTER = "The message is not associated to the adopter";

	// Adopter
	public static final String ADOPTER_NOT_FOUND = "The adopter with the given id was not found";
	public static final String ADOPTER_NOT_VALID = "Adopter data is not valid";
	public static final String ADOPTER_EMAIL_EXISTS = "There is already an adopter registered with that email";
	public static final String ADOPTER_ASSOCIATED_ADOPTIONS = "Unable to delete the adopter because it has associated adoptions";
	public static final String ADOPTER_ASSOCIATED_MESSAGES = "Unable to delete the adopter because it has associated messages";
	public static final String ADOPTER_ASSOCIATED_REVIEWS = "Unable to delete the adopter because it has associated reviews";

	// Review
	public static final String REVIEW_NOT_FOUND = "The review with the given id was not found";
	public static final String REVIEW_RATING_INVALID = "Review rating must be between 1 and 5";
	public static final String REVIEW_NOT_ASSOCIATED_TO_ADOPTER = "The review is not associated to the adopter";

	// Adoption
	public static final String ADOPTION_NOT_FOUND = "The adoption with the given id was not found";
	public static final String ADOPTION_NOT_VALID = "Adoption data is not valid";
	public static final String ADOPTION_STATUS_INVALID = "Adoption status is not valid";
	public static final String ADOPTION_PET_NOT_AVAILABLE = "The pet is not available for adoption";

	// ReturnRecord
	public static final String RETURN_RECORD_NOT_FOUND = "The adoption does not have an associated return record";
	public static final String RETURN_RECORD_NOT_VALID = "Return record data is not valid";
	public static final String RETURN_RECORD_ALREADY_EXISTS = "The adoption already has an associated return record";

	// TrialCohabitation
	public static final String TRIAL_COHABITATION_NOT_FOUND = "The adoption does not have an associated trial cohabitation";
	public static final String TRIAL_COHABITATION_NOT_VALID = "Trial cohabitation data is not valid";
	public static final String TRIAL_COHABITATION_ALREADY_EXISTS = "The adoption already has an associated trial cohabitation";
	public static final String TRIAL_COHABITATION_DATE_INVALID = "Trial cohabitation end date must be after the start date";

	// Notification
	public static final String NOTIFICATION_NOT_FOUND = "The notification with the given id was not found";
	public static final String NOTIFICATION_NOT_VALID = "Notification data is not valid";
	public static final String NOTIFICATION_CHANNEL_INVALID = "Notification channel must be EMAIL, SMS or PUSH";
	public static final String NOTIFICATION_NOT_ASSOCIATED_TO_SHELTER = "The notification is not associated to the shelter";

	// Veterinarian
	public static final String VETERINARIAN_NOT_FOUND = "The veterinarian with the given id was not found";
	public static final String VETERINARIAN_NOT_VALID = "Veterinarian data is not valid";
	public static final String VETERINARIAN_ASSOCIATED_LIFE_EVENTS = "Unable to delete the veterinarian because it has associated life events";
	public static final String VETERINARIAN_ASSOCIATED_ADOPTIONS = "Unable to delete the veterinarian because it has associated adoptions";

	// VaccinationRecord
	public static final String VACCINATION_RECORD_NOT_FOUND = "The pet does not have an associated vaccination record";
	public static final String VACCINATION_RECORD_NOT_VALID = "Vaccination record data is not valid";
	public static final String VACCINATION_RECORD_ALREADY_EXISTS = "The pet already has an associated vaccination record";

	// LifeEvent
	public static final String LIFE_EVENT_NOT_FOUND = "The life event with the given id was not found";
	public static final String LIFE_EVENT_NOT_VALID = "Life event data is not valid";
	public static final String LIFE_EVENT_NOT_ASSOCIATED_TO_PET = "The life event is not associated to the pet";

	private ErrorMessage() {
		throw new IllegalStateException("Utility class");
	}
}