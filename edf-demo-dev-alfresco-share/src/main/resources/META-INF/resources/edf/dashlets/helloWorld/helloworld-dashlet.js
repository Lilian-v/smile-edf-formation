if (typeof Edf == "undefined" || !Edf) {
	var Edf = {};
}

if (typeof Edf.dashlet == "undefined" || !Edf.dashlet) {
	Edf.dashlet = {};
}

/**
 * Sample Hello World dashboard Surf component.
 *
 * @namespace MyCompany.dashlet
 * @class MyCompany.dashlet.HelloWorld
 * @author
 */
(function() {
	/**
	 * YUI Library aliases
	 */
	var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;

	/**
	 * Alfresco Slingshot aliases
	 */
	var $html = Alfresco.util.encodeHTML, $combine = Alfresco.util.combinePaths;

	/**
	 * Dashboard HelloWorld constructor.
	 *
	 * @param {String} htmlId The HTML id of the parent element
	 * @return {MyCompany.dashlet.HelloWorld} The new component instance
	 * @constructor
	 */
	Edf.dashlet.HelloWorld = function HelloWorld_constructor(htmlId) {
		return Edf.dashlet.HelloWorld.superclass.constructor.call(this, "Edf.dashlet.HelloWorld", htmlId);
	};

	/**
	 * Extend from Alfresco.component.Base and add class implementation
	 */
	YAHOO.extend(Edf.dashlet.HelloWorld, Alfresco.component.Base, {
		/**
		 * Object container for initialization options
		 *
		 * @property options
		 * @type object
		 */
		options : {},

		/**
		 * Fired by YUI when parent element is available for scripting
		 *
		 * @method onReady
		 */
		onReady : function HelloWorld_onReady() {
			this.widgets.testButton = Alfresco.util.createYUIButton(this,
					"testButton", this.onButtonClick);
		},

		/**
		 * Button click event handler
		 *
		 * @method onButtonClick
		 */
		onButtonClick : function HelloWorld_onButtonClick(e) {
			Alfresco.util.PopupManager.displayMessage({
				text : "Button clicked in Hello World Dashlet!"
			});
		}
	});
})();