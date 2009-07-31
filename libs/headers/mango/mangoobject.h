
#ifndef __MANGO_OBJECT_H__
#define __MANGO_OBJECT_H__

#include <stdlib.h>

typedef struct _MangoObject MangoObject;
typedef struct _MangoClass MangoClass;

struct _MangoObject
{
    const MangoClass *class;
};

/**
 * NOTE: ALL fields are mandatory and (mostly) compiler generated (destroy has some optional user code)
 */
struct _MangoClass
{
    size_t size; /// number of bytes to allocate for a new instance of this class
    const char *name; /// human readable representation of the name of this class
	const struct _MangoClass* super; /// MangoObject *to instance of super-class
    void (*initialize)(MangoObject *this); /// initializes default values for a new instance of this class
    void (*destroy)(MangoObject *this); /// cleans up any objects belonging to this instance
};

/**
 *
 */
MangoObject *mango_object_new(const MangoClass *type);

/**
 *
 */
MangoObject *mango_object_copy(const MangoObject *this);

/**
 *
 */
MangoObject *mango_object_ref(MangoObject *this);

/**
 *
 */
void mango_object_unref(MangoObject *this);

/**
 *
 */
void mango_object_delete(MangoObject *this);

/**
 * 
 */
void (*memb_func_ptr(const MangoObject *this, void (*func)()))();

#endif // __MANGO_OBJECT_H__
