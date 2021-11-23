package fr.edf.policies;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import fr.edf.model.EdfModel;

public class RemoveArchiveAspectOnUpdateDateDocumentPolicy implements OnUpdatePropertiesPolicy, InitializingBean {
	private static final Logger logger = Logger.getLogger(RemoveArchiveAspectOnUpdateDateDocumentPolicy.class);
	
	@Autowired
	private PolicyComponent policyComponent;
	@Autowired
	private NodeService nodeService;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.policyComponent.bindClassBehaviour(OnUpdatePropertiesPolicy.QNAME,
				EdfModel.ASPECT_DOCUMENT_ARCHIVE, // Aspect / type que le node doit avoir pour que la policy se déclenche
				new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));
	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		logger.debug("Déclenchement policy RemoveArchiveAspectOnUpdateDateDocumentPolicy");
		
		// Toujours testé l'existence du noeud, les policies se déclenchent parfois sur des noeuds temporaires système qui n'existent plus à la fin de la transaction
		if (!nodeService.exists(nodeRef)) {
			return ;
		}
		
		Date dateDocumentBefore = (Date) before.get(EdfModel.PROP_DOCUMENT_COMPTABLE_DATE_FACTURATION);
		Date dateDocumentAfter = (Date) after.get(EdfModel.PROP_DOCUMENT_COMPTABLE_DATE_FACTURATION);
		
		if (dateDocumentAfter == null || (dateDocumentBefore != null && !dateDocumentAfter.equals(dateDocumentBefore))) {
			// La date a été modifiée, on retire l'aspect DocumentArchivé
			logger.debug("Removing aspect 'edf:documentArchive' on node " + nodeRef);
			nodeService.removeAspect(nodeRef, EdfModel.ASPECT_DOCUMENT_ARCHIVE);
		}
	}

}
