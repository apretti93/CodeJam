console.log("hello body");

$( document ).ready(function() {
console.log("hello " + $('.tooltip').length);
console.log(jQuery.fn.jquery);

$('.tooltip').qtip({ // Grab some elements to apply the tooltip to
    content: true,
     position: {
        my: 'top left',  // Position my top left...
        at: 'center', // at the bottom right of...
        target: 'event' // my target
    },
    style: {
        width: '800px'
    },
    hide: {
            // event: 'click',
             //inactive: 1200,
             fixed: true
         }
    /*position: {
        viewport: $(window)
    }*/
});    

console.log( jQuery("#tree").length );

  jQuery("#tree")
	.bind('loaded.jstree', function(e, data)
	{
		/**
		* Open nodes on load (until x'th level)
		*/
		var depth = 3;
		data.inst.get_container().find('li').each(function(i) 
		{
			if(data.inst.get_path($(this)).length<=depth)
			{
				data.inst.open_node($(this));
			}
		});
	})
    .jstree({
		"core" : { "animation" : 5 },
		"themes" : {
			"theme" : "classic",
			"dots" : true,
			"icons" : false
		},
		"plugins" : [ "themes", "html_data" ]
       

    });

	

});