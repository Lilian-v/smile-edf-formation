if (typeof Edf == undefined || !Edf) {
	var Edf = {};
}

(function() {
	// Constructeur dans lequel on appelle le constructeur parent
	Edf.DocumentList = function CustomDocumentList_constructor(htmlId) {
		Edf.DocumentList.superclass.constructor.call(this, htmlId);
		
		// Re-register with our own name
		this.name = "Edf.DocumentList";
		Alfresco.util.ComponentManager.reregister(this);
		return this;
	};

	// Extend YAHOO pour permettre de surcharger les méthodes du composant Alfresco
	YAHOO.extend(Edf.DocumentList, Alfresco.DocumentList, {
		// Méthode quand le composant est prêt à être chargé
		onReady : function CustomDL_onReady() {
			// On appelle la méthode parent
			Edf.DocumentList.superclass.onReady.call(this);
			
			// On récupère la barre de menu puis la partie de droite
			var rightTopBar = YAHOO.util.Dom.getElementsByClassName("header-bar")[0].getElementsByClassName("right")[0];
			// Et notre bouton masqué
			var edfButton = YAHOO.util.Dom.get(this.id + "-edf-button");
			
			// Déplacement du bouton au bon endroit
			rightTopBar.appendChild(edfButton);
			
			// On le rend visible et on ajoute une fonction au clic
			YAHOO.util.Dom.setStyle(edfButton, "display", "block")
			edfButton.onclick = this.onEdfButtonClick;
		},
		

		onEdfButtonClick : function CustomDL_onEdfButtonClick(e, p_obj) {
			// Création d'une modale
			var confirmDialog = new YAHOO.widget.SimpleDialog('edfDialog', {
				fixedcenter: true,
				visible: false,
				draggable: false,
				close: true,
				constraintoviewport: true,
				modal:true,
				buttons: [
					{
						text:"Oui", 
						handler: {
							fn : function(e) {
								confirmDialog.hide();
								
								// Redirection vers la page URL
								var edfPageUrl = "/share/page";
								// si on est dans un site on veut rester dans le site
								if (Alfresco.constants.SITE != "") {
									edfPageUrl += "/site/" + Alfresco.constants.SITE;
								}
								
								edfPageUrl += "/edfhome";
								window.location.href = edfPageUrl;
							}
						},
						scope : this,
						isDefault : true
					}, 
					{
						text:"Non", 
						handler: {
							fn : function(e) {
								confirmDialog.hide();
							}
						},
						scope : this
					}
				]
			});
			
			// Affichage de la modale
			confirmDialog.setHeader("Informations EDF");
			confirmDialog.setBody("Souhaitez-vous voir la page d'accueil EDF ?");
			confirmDialog.render(document.body);
			confirmDialog.show();
		}

	});

})();
