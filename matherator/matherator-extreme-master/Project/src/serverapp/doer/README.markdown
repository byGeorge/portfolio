The `serverapp.doer` package contains a number of special action handlers related to the operations of the server. Their hallmark is that they take proto-blocks from the caller to assist in the running of the job.

The **database doer** classes, for instance, perform database queries and updates, opaquely and safely. The return value is negatory when an SQL error occurred. A *fetcher* runs the given block (or, a block-like java monstrosity) for each result, and optionally maps the output of each run into a returned list. An *updater* does not take a block.

Similar doers which do not apply specifically to the server should be organized into similar doer packages in appropriate places in the project.
