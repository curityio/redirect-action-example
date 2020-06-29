RedirectAction Authentication Action Plugin
=============================================

An example authentication action plugin for the Curity Identity Server. The plugin redirects the user to a given URL,
and only continues with the authentication if it can verify that the external action succeeded. It achieves that by generating
a random UUID and redirecting the user to the external system and passing the UUID in a ``status`` query parameter. When
the authentication is continued, the action makes a GET request to the external system again adding the ``status`` parameter
with the same UUID. The action expects the endpoint to return a 200 response with JSON containing two fields: ``externalUserId``
and ``externalStatus``. Values of these two fields are then added to the Authorization Parameters.

Building the Plugin
~~~~~~~~~~~~~~~~~~~

Ensure that the Curity Nexus server is `configured in your Maven settings
<https://developer.curity.io/docs/latest/developer-guide/plugins/index.html#access-to-the-curity-release-repository>`_.
Then, build the plugin by issuing the command ``mvn package``. This will produce a JAR file in the ``target`` directory,
which can be installed.

Installing the Plugin
~~~~~~~~~~~~~~~~~~~~~

To install the plugin, copy the compiled JAR (and all of its dependencies) into the :file:`${IDSVR_HOME}/usr/share/plugins/redirect-action`
on each node, including the admin node. For more information about installing plugins, refer to the `curity.io/plugins`_.

Required Dependencies
"""""""""""""""""""""

For a list of the dependencies and their versions, run ``mvn dependency:list``. Ensure that all of these are installed in
the plugin group; otherwise, they will not be accessible to this plug-in and run-time errors will result.

More Information
~~~~~~~~~~~~~~~~

Please visit `curity.io`_ for more information about the Curity Identity Server.

.. _curity.io/plugins: https://support.curity.io/docs/latest/developer-guide/plugins/index.html#plugin-installation
.. _curity.io: https://curity.io/
