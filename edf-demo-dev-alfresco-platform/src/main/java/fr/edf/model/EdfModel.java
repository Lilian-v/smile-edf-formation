package fr.edf.model;

import org.alfresco.service.namespace.QName;

public interface EdfModel {
	public static final String EDF_URI = "http://www.edf.fr/model/content/1.0";
	
	public static final QName ASPECT_DOCUMENT_COMPTABLE = QName.createQName(EDF_URI, "documentComptable");
	public static final QName PROP_DOCUMENT_COMPTABLE_ID = QName.createQName(EDF_URI, "documentComptableID");
	public static final QName PROP_DOCUMENT_COMPTABLE_DATE_FACTURATION = QName.createQName(EDF_URI, "documentComptableDateFacturation");
	
	public static final QName ASPECT_DOCUMENT_ARCHIVE = QName.createQName(EDF_URI, "documentArchive");

}
