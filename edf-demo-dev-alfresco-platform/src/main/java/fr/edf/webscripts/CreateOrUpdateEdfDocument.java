package fr.edf.webscripts;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.edf.model.EdfModel;

public class CreateOrUpdateEdfDocument extends AbstractWebScript {
	private static final Logger logger = Logger.getLogger(CreateOrUpdateEdfDocument.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private NodeService nodeService;
	@Autowired 
	private SearchService searchService;
	@Autowired
	private FileFolderService fileFolderService;
	@Autowired
	private ContentService contentService;
	@Autowired
	private SiteService siteService;
	
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		logger.debug("Début webscript CreateOrUpdateEdfDocument");
		
		// Vérification des paramètres
		String nomDocumentParam = req.getParameter("nomDocument");
		String edfIDParam = req.getParameter("edfID");
		String dateFacturationStr = req.getParameter("dateFacturation");
		if (StringUtils.isEmpty(nomDocumentParam) || StringUtils.isEmpty(edfIDParam) || StringUtils.isEmpty(dateFacturationStr)) {
			throw new IllegalStateException("Parameters 'nomDocument', 'edfID' and 'dateFacturation' are mandatory");
		}
		Date dateFacturationParam;
		try {
			dateFacturationParam = dateFormat.parse(dateFacturationStr);
		} catch (ParseException e) {
			throw new IllegalStateException("Parameter 'dateFacturation' value '" + dateFacturationStr + "' has an invalid format, expected pattern " + dateFormat.toPattern());
		}
		
		NodeRef nodeToUpdate;
		
		// Vérrification si le document existe déjà
		String searchQuery = "=edf:documentComptableID:\"" + edfIDParam + "\"";
		List<NodeRef> searchResults = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, SearchService.LANGUAGE_FTS_ALFRESCO, searchQuery).getNodeRefs();
		if (!searchResults.isEmpty()) {
			nodeToUpdate = searchResults.get(0);
			logger.info("Updating existing node " + nodeToUpdate);
		}
		else {
			// Création d'un nouveau document
			// On doit spécifier le dossier parent, on va se mettre à la racine du site de démo
			String siteShortName = "swsdp";
			SiteInfo siteDepot = siteService.getSite(siteShortName);
			if (siteDepot == null) {
				throw new IllegalStateException("Site '" + siteShortName + "' not found");
			}
			NodeRef documentLibraryNodeRef = siteService.getContainer(siteDepot.getShortName(), SiteService.DOCUMENT_LIBRARY);
			
			// Création d'un nouveau document
			// TODO si passage en prod :  vérifier que le nom est un nom valide, et qu'il n'existe pas déjà dans le dossier
			nodeToUpdate = fileFolderService.create(documentLibraryNodeRef, nomDocumentParam, ContentModel.TYPE_CONTENT).getNodeRef();
		}
		
		// Mise à jour des métadonnées du document
		nodeService.setProperty(nodeToUpdate, ContentModel.PROP_NAME, nomDocumentParam);
		nodeService.setProperty(nodeToUpdate, EdfModel.PROP_DOCUMENT_COMPTABLE_ID, edfIDParam);
		nodeService.setProperty(nodeToUpdate, EdfModel.PROP_DOCUMENT_COMPTABLE_DATE_FACTURATION, dateFacturationParam);
		
		// Ecriture du contenu du document
		ContentWriter nodeWriter = contentService.getWriter(nodeToUpdate, ContentModel.PROP_CONTENT, true);
		nodeWriter.guessEncoding();
		nodeWriter.guessMimetype(nomDocumentParam);
		try (InputStream is = req.getContent().getInputStream()) {
			nodeWriter.putContent(is);
		}
		
		// Statut indiquant la création ok et message de succès
		res.setStatus(201);
		try (Writer writer = res.getWriter()) {
			writer.write("Création ou mise à jour du document " + nodeToUpdate + " réalisée avec succès");
		}
		
		logger.debug("Fin webscript CreateOrUpdateEdfDocument");
	}
}
